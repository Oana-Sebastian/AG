import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import javax.swing.*;

import static java.lang.Math.sqrt;

import java.io.IOException;





public class MyPanel extends JPanel {
    private Node draggedNode=null;
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    private int max_nodes=100;
    private int[][]adjMatrix;
    private int[][] flowMatrix;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    boolean rightDragging=false;
    private Set<Node> cutNodes = new HashSet<>();
    boolean minCutDraw=false;
    private void writeAdjMatrixToFile(String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            // Write matrix dimensions first (optional)
            writer.write("Adjacency Matrix (" + (nodeNr - 1) + " x " + (nodeNr - 1) + "):");
            writer.newLine();

            // Write each row of the matrix
            for (int i = 0; i < nodeNr - 1; i++) {
                for (int j = 0; j < nodeNr - 1; j++) {
                    writer.write(adjMatrix[i][j] + " ");
                }
                writer.newLine();
            }

            writer.close(); // Close the writer
            System.out.println("Adjacency matrix written to " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private double distance(Point coord1, Point coord2)
    {
        return sqrt((coord2.x-coord1.x)*(coord2.x-coord1.x)+(coord2.y-coord1.y)*(coord2.y-coord1.y));
    }




    public MyPanel() {
        JButton calcFlowButton = new JButton("Calculate Max Flow");
        calcFlowButton.addActionListener(e -> selectSourceAndSink());
        add(calcFlowButton);
        listaNoduri = new Vector<Node>();
        listaArce = new Vector<Arc>();
        adjMatrix = new int[max_nodes][max_nodes];
        flowMatrix = new int[max_nodes][max_nodes];
        writeAdjMatrixToFile("matrix.txt");

        // borderul panel-ului
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter() {
            //evenimentul care se produce la apasarea mouse-ului
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e))
                    pointStart = e.getPoint();
                else if (SwingUtilities.isRightMouseButton(e)) {
                    draggedNode = getNodeAtPoint(e.getPoint());
                    rightDragging = draggedNode != null;
                }
            }

            //evenimentul care se produce la eliberarea mousse-ului
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!isDragging) {
                        addNode(e.getX(), e.getY());
                        writeAdjMatrixToFile("matrix.txt");
                    } else {
                        Point first = null, second = null;
                        Node firstNode = null, secondNode = null;
                        for (Node i : listaNoduri) {
                            if (first == null && second == null && distance(pointStart, i.getCoord()) <= node_diam / 2.0 + 1) {
                                {
                                    first = new Point(i.getCoordX(), i.getCoordY());
                                    firstNode = i;
                                    break;
                                }
                            }
                        }
                        for (Node i : listaNoduri) {
                            if ((second == null && first != null) && distance(pointEnd, i.getCoord()) <= node_diam / 2.0 + 1) {
                                {
                                    second = new Point(i.getCoordX(), i.getCoordY());
                                    secondNode = i;
                                    break;
                                }
                            }
                        }

                        boolean isArc = false;
                        if (first != null && second != null) {

                            for (Arc i : listaArce) {
                                if (i.getStart().equals(first) && i.getEnd().equals(second)) {
                                    isArc = true;
                                    break;
                                }
                            }

                            if (!isArc && distance(pointStart, pointEnd) > node_diam) {
                                addArc(firstNode, secondNode);
                            }
                        }
                        pointStart = null;
                        pointEnd = null;
                        isDragging = false;
                        repaint();
                    }
                } else if (SwingUtilities.isRightMouseButton(e) && draggedNode != null) {
                    draggedNode = null;
                    rightDragging = false;
                    repaint();
                }

            }
        });



        addMouseMotionListener(new MouseMotionAdapter() {
            //evenimentul care se produce la drag&drop pe mousse
            public void mouseDragged(MouseEvent e) {

                if (SwingUtilities.isLeftMouseButton(e)) {
                    pointEnd = e.getPoint();
                    isDragging = true;
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e) && draggedNode != null) {
                    Point newCoords = e.getPoint();
                    boolean isDist = true;
                    for (Node i : listaNoduri) {
                        if (draggedNode.getNumber() != i.getNumber()) {
                            if (distance(newCoords, i.getCoord()) < 1.5 * node_diam) {
                                isDist = false;
                                break;
                            }
                        }

                    }
                    if (isDist && (e.getY() < 576 - 1.6 * node_diam && e.getY() > node_diam / 2 && e.getX() < 1024 - node_diam && e.getX() > node_diam / 2)) {
                        draggedNode.setCoordX(newCoords.x);
                        draggedNode.setCoordY(newCoords.y);
                        updateArcsForNode(draggedNode);
                    } else {
                        draggedNode = null;
                        rightDragging = false;
                    }
                    repaint();
                }
            }

        });

    }



    //metoda care se apeleaza la eliberarea mouse-ului
    private void addNode(int x, int y) {
        minCutDraw=false;
        if(x>1024-node_diam-10)
            x=1024-node_diam-11;
        if(x<node_diam)
            x=node_diam;
        if(y>571-2*node_diam)
            y=570-2*node_diam;
        if(y<node_diam+10)
            y=node_diam;
        boolean isDist=true;
        for(int i=0;i<listaNoduri.size();i++)
        {
            int node_coord_x=listaNoduri.elementAt(i).getCoordX();
            int node_coord_y=listaNoduri.elementAt(i).getCoordY();
            if(sqrt((node_coord_x-x)*(node_coord_x-x)+(node_coord_y-y)*(node_coord_y-y))<3*node_diam)
                isDist=false;
        }
        if(isDist)
        { Node node = new Node(x, y, nodeNr);
            listaNoduri.add(node);
            nodeNr++;
            repaint();
        }
    }

    private void addArc(Node startNode, Node endNode) {
        minCutDraw = false;

        // Prompt for capacity
        String capacityStr = JOptionPane.showInputDialog("Enter capacity for arc "
                + startNode.getNumber() + " -> " + endNode.getNumber() + ":");

        // Prompt for flow
        String flowStr = JOptionPane.showInputDialog("Enter flow for arc "
                + startNode.getNumber() + " -> " + endNode.getNumber() + ":");

        // Validate and parse inputs
        int capacity = 0; // Default to 0 if not provided
        int flow = 0; // Default to 0 if not provided

        if (capacityStr != null && !capacityStr.isEmpty()) {
            try {
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid capacity entered. Defaulting to 0.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        if (flowStr != null && !flowStr.isEmpty()) {
            try {
                flow = Integer.parseInt(flowStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid flow entered. Defaulting to 0.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        // Create the arc
        Arc arc = new Arc(startNode, endNode, capacity, flow);
        listaArce.add(arc);

        // Update matrices
        adjMatrix[startNode.getNumber() - 1][endNode.getNumber() - 1] = capacity;
        flowMatrix[startNode.getNumber() - 1][endNode.getNumber() - 1] = flow;

        // Save to file and repaint
        writeAdjMatrixToFile("matrix.txt");
        repaint();
    }


    //se executa atunci cand apelam repaint()
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);//apelez metoda paintComponent din clasa de baza
        g.drawString("This is my Graph!", 10, 20);
        g.drawString("Numar noduri: "+listaNoduri.size(),10, 40);
        g.drawString("Numar arce: "+ listaArce.size(),10,60);
        //deseneaza arcele existente in lista
		/*for(int i=0;i<listaArce.size();i++)
		{
			listaArce.elementAt(i).drawArc(g);
		}*/


        for (Arc a : listaArce)
        {
            g.setColor(Color.BLACK);
            a.drawArc(g);
            int startIdx = a.getStartNumber() - 1;
            int endIdx = a.getEndNumber() - 1;
            // Afișează fluxul pe arc
            String fluxString = flowMatrix[startIdx][endIdx] + "/" + adjMatrix[startIdx][endIdx];
            drawFlowText(g, a, fluxString);
        }

        //deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null&& pointEnd!=null){
            g.setColor(Color.GRAY);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
            double v1= sqrt((Math.pow(pointStart.x-pointEnd.x,2))+Math.pow(pointStart.y-pointEnd.y,2));
            double u1=(pointStart.x-pointEnd.x)/v1;
            double u2=(pointStart.y-pointEnd.y)/v1;
            int arrowSize=30;
            double angle = Math.atan2(pointEnd.y - pointStart.y, pointEnd.x-pointStart.x);
            int x1 = (int) (pointEnd.x - arrowSize * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (pointEnd.y - arrowSize * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (pointEnd.x - arrowSize * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (pointEnd.y - arrowSize * Math.sin(angle +Math.PI/6));
            g.drawLine((int)(pointEnd.x+ node_diam/2*u1), (int)(pointEnd.y+ node_diam/2*u2), x1, y1);
            g.drawLine((int)(pointEnd.x+ node_diam/2*u1), (int)(pointEnd.y+ node_diam/2*u2), x2, y2);
        }


        //deseneaza lista de noduri
        for(int i=0; i<listaNoduri.size(); i++)
        {
            g.setColor(Color.RED);
            listaNoduri.elementAt(i).drawNode(g, node_diam);
        }
		/*for (Node nod : listaNoduri)
		{
			nod.drawNode(g, node_diam, node_Diam);
		}*/

        if (cutNodes != null) { // cutNodes ar trebui să fie un set de noduri care formează tăietura minimă
            g.setColor(Color.RED);
            for (Arc arc : listaArce) {
                int u = arc.getStartNumber() - 1;
                int v = arc.getEndNumber() - 1;
                // Dacă arc merge de la un nod accesibil la unul inaccesibil (tăietura minimă)
                if (cutNodes.contains(arc.getStartNode()) && !cutNodes.contains(arc.getEndNode())) {
                    // Evidențiază acest arc
                    g.setColor(Color.RED);
                    arc.drawArc(g); // Desenează arc roșu pentru a marca tăietura minimă
                }
            }
        }

        if(minCutDraw)
        {
            for(Node node:cutNodes)
            {
                g.setColor(Color.BLUE);
                node.drawNode(g,node_diam);
            }

            if (cutNodes != null) { // cutNodes ar trebui să fie un set de noduri care formează tăietura minimă
                g.setColor(Color.RED);
                for (Arc arc : listaArce) {
                    int u = arc.getStartNumber() - 1;
                    int v = arc.getEndNumber() - 1;
                    // Dacă arc merge de la un nod accesibil la unul inaccesibil (tăietura minimă)
                    if (cutNodes.contains(arc.getStartNode()) && !cutNodes.contains(arc.getEndNode())) {
                        // Evidențiază acest arc
                        g.setColor(Color.RED);
                        arc.drawArc(g); // Desenează arc roșu pentru a marca tăietura minimă
                    }
                }
            }
        }
    }

    private void drawFlowText(Graphics g, Arc arc, String text) {
        int midX = (arc.getStart().x + arc.getEnd().x) / 2;
        int midY = (arc.getStart().y + arc.getEnd().y) / 2;

        // Calculăm offsetul pentru text
        int dx = arc.getEnd().x - arc.getStart().x;
        int dy = arc.getEnd().y - arc.getStart().y;
        double distance = sqrt(dx * dx + dy * dy);
        int offsetX = (int) (dx / distance * 20);
        int offsetY = (int) (dy / distance * 20);

        // Poziționăm textul
        int textX = midX - offsetX;
        int textY = midY - offsetY;

        g.setColor(Color.BLUE);
        g.drawString(text, textX, textY);
    }

    private Node getNodeAtPoint(Point p) {
        for (Node node : listaNoduri) {
            if (distance(p, node.getCoord()) <= node_diam / 2.0) {
                return node;
            }
        }
        return null; // Niciun nod nu a fost găsit la coordonatele date
    }
    private void updateArcsForNode(Node node) {
        for (Arc arc : listaArce) {
            if (distance(arc.getStart(),node.getCoord())<=node_diam*0.8) {
                arc.setStart(node.getCoord());
            } else if (distance(arc.getEnd(),node.getCoord())<=node_diam*0.8) {
                arc.setEnd(node.getCoord());
            }
        }
    }


    private boolean bfs(int source, int sink, int[] parent) {
        boolean[] visited = new boolean[nodeNr];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int i = 0; i < nodeNr; i++) {
                if (!visited[i] && adjMatrix[current][i] - flowMatrix[current][i] > 0) {
                    parent[i] = current;
                    visited[i] = true;
                    queue.add(i);
                    if (i == sink) return true;
                }
            }
        }
        return false;
    }

    public int fordFulkerson(int source, int sink) {
        int[] parent = new int[nodeNr];
        int maxFlow = 0;

        while (bfs(source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, adjMatrix[u][v] - flowMatrix[u][v]);
            }

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                flowMatrix[u][v] += pathFlow;
                flowMatrix[v][u] -= pathFlow;
            }

            maxFlow += pathFlow;
        }

        // Actualizează fluxul în arce
        for (Arc arc : listaArce) {
            int u = arc.getStartNode().getNumber() - 1;
            int v = arc.getEndNode().getNumber() - 1;
            arc.setFlow(flowMatrix[u][v]);  // Setează fluxul final
        }


        highlightMinCut(source);
        repaint();  // Redesenare pentru actualizarea grafică
        return maxFlow;
    }

    private void selectSourceAndSink() {
        minCutDraw=true;
        String sourceStr = JOptionPane.showInputDialog("Enter source node:");
        String sinkStr = JOptionPane.showInputDialog("Enter sink node:");

        if (sourceStr != null && !sourceStr.isEmpty() && sinkStr != null&& !sinkStr.isEmpty()) {
            int source = Integer.parseInt(sourceStr) - 1;
            int sink = Integer.parseInt(sinkStr) - 1;

            int maxFlow = fordFulkerson(source, sink);
            JOptionPane.showMessageDialog(this, "Maximum Flow: " + maxFlow);
        }
    }

    private void highlightMinCut(int source) {
        boolean[] reachable = new boolean[nodeNr];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        reachable[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int i = 0; i < nodeNr; i++) {
                if (!reachable[i] && adjMatrix[current][i] - flowMatrix[current][i] > 0) {
                    reachable[i] = true;
                    queue.add(i);
                }
            }
        }

        cutNodes.clear();
        for (Node node : listaNoduri) {
            if (reachable[node.getNumber() - 1]) {
                cutNodes.add(node);
            }
        }
    }
}

