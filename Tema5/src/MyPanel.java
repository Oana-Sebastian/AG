import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import javax.swing.*;

import static java.lang.Math.sqrt;

import java.io.IOException;
import java.util.List;


public class MyPanel extends JPanel {
    private Node draggedNode=null;
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Edge> listaMuchii;
    private static Vector<Edge> muchiiMinimizate;
    private int max_nodes=100;
    private int[][]adjMatrix;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    boolean rightDragging=false;
    private List<List<Integer>> adjList=new ArrayList<>();
    private List<List<Edge>> adjEdge=new ArrayList<>();
    private JButton primButton;
    private JButton kruskalButton;
    private boolean minimizedPaint =false;
    private int cost;
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
            for(int i=0;i<listaNoduri.size();i++) {
                System.out.print((i+1)+ ":");
                for (int j = 0; j < adjList.get(i).size(); j++)
                    System.out.print((adjList.get(i).get(j)+1)+" ");
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private double distance(Point coord1, Point coord2)
    {
        return sqrt((coord2.x-coord1.x)*(coord2.x-coord1.x)+(coord2.y-coord1.y)*(coord2.y-coord1.y));
    }
    public MyPanel()
    {
        listaNoduri = new Vector<Node>();
        listaMuchii = new Vector<Edge>();
        adjMatrix=new int[max_nodes][max_nodes];
        // borderul panel-ului


        setBorder(BorderFactory.createLineBorder(Color.black));

        primButton = new JButton("Algoritmul Prim");
        primButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Algoritmul Prim");
                if(!listaNoduri.isEmpty()) {
                    primMST(adjEdge, listaNoduri.size());
                    minimizedPaint = true;
                    repaint();
                }
            }
        });

        kruskalButton = new JButton("Algoritmul Kruskal");
        kruskalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               muchiiMinimizate =kruskal(listaNoduri.size(),listaMuchii);
               minimizedPaint =true;
               repaint();
                System.out.println("Algoritmul Kruskal");
                for (Edge edge : muchiiMinimizate) {
                    System.out.println((edge.getStartNr()+1) + " - " + (edge.getEndNr()+1)+" " + edge.getWeight());
                }

            }
        });

        this.add(primButton);
        this.add(kruskalButton);

        addMouseListener(new MouseAdapter() {
            //evenimentul care se produce la apasarea mouse-ului
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e))
                    pointStart = e.getPoint();
                else if(SwingUtilities.isRightMouseButton(e)) {
                    draggedNode=getNodeAtPoint(e.getPoint());
                    rightDragging=draggedNode!=null;
                }
            }
            //evenimentul care se produce la eliberarea mousse-ului
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
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
                            if ((second == null && first != null) && distance(pointEnd,i.getCoord()) <= node_diam / 2.0 + 1) {
                                {
                                    second = new Point(i.getCoordX(), i.getCoordY());
                                    secondNode = i;
                                    break;
                                }
                            }
                        }

                        boolean isEdge = false;
                        if (first != null && second != null) {

                            for (Edge i : listaMuchii) {
                                if (i.getStart().equals(first) && i.getEnd().equals(second)||i.getStart().equals(second)&&i.getEnd().equals(first)) {
                                    isEdge = true;
                                    break;
                                }
                            }

                            if (!isEdge && distance(pointStart, pointEnd) > node_diam) {
                                String weightInput = JOptionPane.showInputDialog("Enter weight for the edge:");
                                if(weightInput!=null && !weightInput.isEmpty())
                                {
                                int weight = Integer.parseInt(weightInput);
                                Edge edge = new Edge(firstNode, secondNode,weight);
                                listaMuchii.add(edge);
                                adjList.get(firstNode.getNumber()-1).add(secondNode.getNumber()-1);
                                adjList.get(secondNode.getNumber()-1).add(firstNode.getNumber()-1);
                                adjEdge.get(firstNode.getNumber()-1).add(edge);
                                adjEdge.get(secondNode.getNumber()-1).add(new Edge(secondNode,firstNode,weight));
                                adjMatrix[firstNode.getNumber() - 1][secondNode.getNumber() - 1] = weight;
                                adjMatrix[secondNode.getNumber() - 1][firstNode.getNumber() - 1] =weight;
                                writeAdjMatrixToFile("matrix.txt");
                            }
                            }
                        }
                        pointStart = null;
                        pointEnd = null;
                        isDragging = false;
                        repaint();
                    }
                }
                else if(SwingUtilities.isRightMouseButton(e)&&draggedNode!=null)
                {
                    draggedNode=null;
                    rightDragging=false;
                    repaint();
                }

            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            //evenimentul care se produce la drag&drop pe mousse
            public void mouseDragged(MouseEvent e) {

                if(SwingUtilities.isLeftMouseButton(e)) {
                    pointEnd = e.getPoint();
                    isDragging = true;
                    repaint();
                }
                else if(SwingUtilities.isRightMouseButton(e)&&draggedNode!=null)
                {
                    Point newCoords=e.getPoint();
                    boolean isDist=true;
                    for(Node i:listaNoduri)
                    {
                        if(draggedNode.getNumber()!=i.getNumber())
                        { if(distance(newCoords,i.getCoord())<1.2*node_diam) {
                            isDist=false;
                            break;
                        }
                        }

                    }
                    if(isDist&& (e.getY()<576-1.6*node_diam && e.getY()>node_diam/2 && e.getX()<1024-node_diam && e.getX()>node_diam/2)) {
                        draggedNode.setCoordX(newCoords.x);
                        draggedNode.setCoordY(newCoords.y);
                        updateArcsForNode(draggedNode);
                    }
                    else {
                        draggedNode=null;
                        rightDragging=false;
                    }
                    repaint();
                }
            }
        });
    }
    public void primMST(List<List<Edge>> graph, int N) {
        muchiiMinimizate=null;
        muchiiMinimizate=new Vector<>();
        cost=0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        int[] v = new int[N];
        Arrays.fill(v, Integer.MAX_VALUE);

        //Lista de noduri incluse in arborele minim
        boolean[] N1 = new boolean[N];

        // Array to store the parent of each vertex in the MST (e(y barat))
        int[] e = new int[N];
        Arrays.fill(e, -1);

        // Start with the first vertex (v(1) = 0)
        v[0] = 0;
        pq.offer(new int[]{0, 0}); // (key, vertex)

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int x = current[1];


            if (N1[x]) continue;

            N1[x] = true; // Include vertex in MST (N1 = N1 U {x})

            // Process all adjacent vertices
            for (Edge edge : graph.get(x)) {
                int y = edge.getEndNr();  // Adjacent vertex
                int weight = edge.getWeight();

                // If y is not in MST and weight is smaller than v[y]
                if (!N1[y] && weight < v[y]) {
                    v[y] = weight;
                    e[y] = x; // Update parent of y
                    pq.offer(new int[]{v[y], y});
                }
            }
            // Add edge e(y) to MST if y != 1
            if (x != 0) {
                // Add edge (e(x), x) to the MST
                cost+=v[x];
                muchiiMinimizate.add(new Edge(listaNoduri.elementAt(e[x]), listaNoduri.elementAt(x), v[x]));
                System.out.println((e[x] + 1) + " - " + (x + 1) + "\t" + v[x]);
            }
        }
    }

    public Vector<Edge> kruskal(int V, Vector<Edge> edges) {
        // Sort the edges by weight
        cost=0;
        edges.sort(Comparator.comparingInt(Edge::getWeight));

        DisjointSet ds = new DisjointSet(V);
        Vector<Edge> mst = new Vector<>();

        // Process edges
        for (Edge edge : edges) {
            int u = edge.getStartNr();
            int v = edge.getEndNr();

            // If u and v are not in the same set, add this edge to the MST
            if (ds.find(u) != ds.find(v)) {
                ds.union(u, v);
                mst.add(edge);
                cost+=edge.getWeight();
            }
        }

        return mst;
    }


    //metoda care se apeleaza la eliberarea mouse-ului
    private void addNode(int x, int y) {
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
            if(sqrt((node_coord_x-x)*(node_coord_x-x)+(node_coord_y-y)*(node_coord_y-y))<2*node_diam)
                isDist=false;
        }
        if(isDist)
        { Node node = new Node(x, y, nodeNr);
            adjList.add(new ArrayList<>());
            adjEdge.add(new ArrayList<>());
            listaNoduri.add(node);
            nodeNr++;
            repaint();
        }
    }

    //se executa atunci cand apelam repaint()
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);//apelez metoda paintComponent din clasa de baza
        g.drawString("This is my Graph!", 10, 20);
        g.drawString("Numar noduri: "+listaNoduri.size(),10, 40);
        g.drawString("Numar arce: "+listaMuchii.size(),10,60);
        g.drawString("Cost Total Arbore Minim:"+cost,10,80);
        //deseneaza muchiile existente in lista
        if(minimizedPaint ==true) {
            for (Edge a : muchiiMinimizate) {
                a.drawEdge(g);

            }
            minimizedPaint =false;
        }
        else {
            cost=0;
            for (Edge a : listaMuchii) {
                a.drawEdge(g);

            }
        }
        //deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null&& pointEnd!=null){
            g.setColor(Color.GRAY);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
        }

            //deseneaza lista de noduri
            for (int i = 0; i < listaNoduri.size(); i++) {
                g.setColor(Color.RED);
                listaNoduri.elementAt(i).drawNode(g, node_diam);
            }

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
        for (Edge arc : listaMuchii) {
            if (distance(arc.getStart(),node.getCoord())<=node_diam/2.0) {
                arc.setStart(node.getCoord());
            } else if (distance(arc.getEnd(),node.getCoord())<=node_diam/2.0) {
                arc.setEnd(node.getCoord());
            }
        }
    }

}

