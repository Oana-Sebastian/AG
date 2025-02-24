import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;

public class MapViewer extends JFrame {
    private Map<Integer, NodeInfo> nodes;
    private Map<Integer, List<Arc>> graph;
    private List<Integer> shortestPath = new ArrayList<>();
    private int startNode = -1;
    private int endNode = -1;

    private int width = 1920;
    private int height = 1080;
    //private int margin = 50;
    private double scale = 1.0;
    private double offsetX = 0, offsetY=0;
    private Point lastDragPoint = null;

    private double minLon, maxLon, minLat, maxLat;

    public MapViewer(Map<Integer, NodeInfo> nodes, Map<Integer, List<Arc>> graph) {
        this.nodes = nodes;
        this.graph = graph;
        setTitle("Drum Minim - Luxemburg");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        calculateBounds();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedNode = findNearestNode(
                        (int)((e.getX() - offsetX) / scale),
                        (int)((e.getY() - offsetY) / scale)
                );

                if (selectedNode == startNode) {
                    startNode = -1; // Deselect the start node
                } else if (selectedNode == endNode) {
                    endNode = -1; // Deselect the end node
                } else if (startNode != -1 && endNode != -1) {
                    // If both start and end are already set, reset them to the new selection
                    startNode = selectedNode;
                    endNode = -1; // Clear the endNode to let the user select a new one
                } else if (startNode == -1) {
                    startNode = selectedNode; // Set the start node
                } else {
                    endNode = selectedNode; // Set the end node
                }

                // If both points are set, calculate the shortest path
                if (startNode != -1 && endNode != -1) {
                    calculateShortestPath();
                }

                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    offsetX += e.getX() - lastDragPoint.x;
                    offsetY += e.getY() - lastDragPoint.y;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        });
        addMouseWheelListener(e -> {
            double delta = e.getPreciseWheelRotation();
            double zoomFactor = (delta > 0) ? 0.9 : 1.1;
            scale *= zoomFactor;
            repaint();
        });

    }

    // Calculează limitele pentru scalare
    private void calculateBounds() {
        minLon = Double.MAX_VALUE;
        maxLon = Double.MIN_VALUE;
        minLat = Double.MAX_VALUE;
        maxLat = Double.MIN_VALUE;

        for (NodeInfo node : nodes.values()) {
            if (node.lon < minLon) minLon = node.lon;
            if (node.lon > maxLon) maxLon = node.lon;
            if (node.lat < minLat) minLat = node.lat;
            if (node.lat > maxLat) maxLat = node.lat;
        }
    }

    // Metodă pentru scalarea coordonatelor
    private int scaleX(int lon) {
        return (int) ((lon - minLon) * width  / (maxLon - minLon));
    }

    private int scaleY(int lat) {
        return (int) (height- (lat - minLat) * height / (maxLat - minLat));
    }

    // Determină cel mai apropiat nod după scalare
    private int findNearestNode(int x, int y) {
        double minDist = Double.MAX_VALUE;
        int nearestNode = -1;
        for (NodeInfo node : nodes.values()) {
            int scaledX = scaleX((int) node.lon);
            int scaledY = scaleY((int) node.lat);
            double dx = (x - scaledX);
            double dy = (y - scaledY);
            double dist = dx * dx + dy * dy;
            if (dist < minDist) {
                minDist = dist;
                nearestNode = node.id;
            }
        }
        return nearestNode;
    }

    private void calculateShortestPath() {
        if (startNode != -1 && endNode != -1) {
            Map<Integer, Integer> prev = dijkstra(graph, startNode);
            shortestPath.clear();
            int pathNode = endNode;

            while (pathNode != startNode && prev.containsKey(pathNode)) {
                shortestPath.add(pathNode);
                pathNode = prev.get(pathNode);
            }
            shortestPath.add(startNode);
            Collections.reverse(shortestPath);
        }
    }

    private Map<Integer, Integer> dijkstra(Map<Integer, List<Arc>> graph, int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> prev = new HashMap<>();
        PriorityQueue<Arc> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.length));

        // Inițializare
        for (int node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        pq.add(new Arc(start, start, 0));

        while (!pq.isEmpty()) {
            Arc current = pq.poll();
            int u = current.to;

            if (current.length > dist.get(u)) {
                continue;
            }

            // Parcurge vecinii
            if (graph.containsKey(u)) {
                for (Arc arc : graph.get(u)) {
                    int v = arc.to;
                    int alt = dist.get(u) + arc.length;

                    if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                        dist.put(v, alt);
                        prev.put(v, u);
                        pq.add(new Arc(u, v, alt));
                    }
                }
            }
        }
        return prev;
    }

    // Desenează harta cu coordonate scalate
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        AffineTransform transform = new AffineTransform();
        transform.translate(offsetX, offsetY);
        transform.scale(scale, scale);
        g2.setTransform(transform);

        g2.setColor(Color.BLACK);
        for (List<Arc> arcs : graph.values()) {
            for (Arc arc : arcs) {
                NodeInfo from = nodes.get(arc.from);
                NodeInfo to = nodes.get(arc.to);
                if (from != null && to != null) {
                    int x1 = scaleX((int)from.lon);
                    int y1 = scaleY((int)from.lat);
                    int x2 = scaleX((int)to.lon);
                    int y2 = scaleY((int)to.lat);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
        }

        if (!shortestPath.isEmpty()) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2.0f));
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                NodeInfo from = nodes.get(shortestPath.get(i));
                NodeInfo to = nodes.get(shortestPath.get(i + 1));
                if (from != null && to != null) {
                    int x1 = scaleX((int)from.lon);
                    int y1 = scaleY((int)from.lat);
                    int x2 = scaleX((int)to.lon);
                    int y2 = scaleY((int)to.lat);
                    g2.drawLine(x1, y1, x2, y2);
                }
            }
            shortestPath.clear();
        }


        g2.setColor(Color.RED);
        if (startNode != -1) {
            NodeInfo start = nodes.get(startNode);
            int x = scaleX((int)start.lon);
            int y = scaleY((int)start.lat);
            g2.fillOval(x - 5, y - 5, 10, 10);  // Cerc mai mare pentru nodul de start
        }

        if (endNode != -1) {
            NodeInfo end = nodes.get(endNode);
            int x = scaleX((int)end.lon);
            int y = scaleY((int)end.lat);
            g2.setColor(Color.GREEN);
            g2.fillOval(x - 5, y - 5, 10, 10);  // Cerc verde pentru nodul final
        }
    }

    public static void main(String[] args) throws Exception {
        XMLParser parser = new XMLParser();
        parser.parseXML("hartaLuxembourg.xml");
        MapViewer viewer = new MapViewer(parser.getNodes(), parser.getGraph());
        viewer.setVisible(true);
    }
}
