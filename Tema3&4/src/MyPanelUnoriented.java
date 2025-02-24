import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import java.util.List;
import javax.swing.*;
import java.util.Random;

import static java.lang.Math.sqrt;

import java.io.IOException;





public class MyPanelUnoriented extends JPanel {
    private Node draggedNode=null;
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Edge> listaMuchii;
    private int max_nodes=100;
    private int[][]adjMatrix;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    boolean rightDragging=false;
    private List<List<Integer>> adjList=new ArrayList<>();
    private boolean[] visited;
    private JButton componenteConexe;
    private List<Color> componentColors;
    private boolean drawConexComponents=false;
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
    public MyPanelUnoriented()
    {
        listaNoduri = new Vector<Node>();
        listaMuchii = new Vector<Edge>();
        adjMatrix=new int[max_nodes][max_nodes];
        // borderul panel-ului

        componenteConexe = new JButton("Componente Conexe");
        componenteConexe.addActionListener(e -> {
            visited = new boolean[listaNoduri.size()];
            List<List<Integer>> components = findConnectedComponents();
            // Afișăm componentele conexe într-un JOptionPane
            generateRandomColors(components.size()); // Generăm culori pentru fiecare componentă
            drawConexComponents=true;
            repaint(); // Redesenăm panoul pentru a afișa culorile
            StringBuilder result = new StringBuilder("Componentele conexe sunt:\n");
            for (List<Integer> component : components) {
                result.append(component).append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString(), "Componente Conexe", JOptionPane.INFORMATION_MESSAGE);
        });

       this.add(componenteConexe);

        setBorder(BorderFactory.createLineBorder(Color.black));
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
                                Edge edge = new Edge(firstNode, secondNode);
                                listaMuchii.add(edge);
                                adjList.get(firstNode.getNumber()-1).add(secondNode.getNumber()-1);
                                adjList.get(secondNode.getNumber()-1).add(firstNode.getNumber()-1);
                                adjMatrix[firstNode.getNumber() - 1][secondNode.getNumber() - 1] = 1;
                                adjMatrix[secondNode.getNumber() - 1][firstNode.getNumber() - 1] =1;
                                writeAdjMatrixToFile("matrix.txt");
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

    private void generateRandomColors(int numComponents) {
        componentColors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numComponents; i++) {
            componentColors.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
    }

    private void DFS(int startNode, List<Integer> component) {
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);
        visited[startNode] = true;

        while (!stack.isEmpty()) {
            int node = stack.pop();
            component.add(node+1);

            for (int neighbor : adjList.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(neighbor);
                }
            }
        }
    }


    public List<List<Integer>> findConnectedComponents() {
        List<List<Integer>> components = new ArrayList<>();
        for (int i = 0; i < listaNoduri.size(); i++) {
            if (!visited[i]) {
                List<Integer> component = new ArrayList<>();
                DFS(i, component);
                components.add(component);
            }
        }
        return components;
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
        //deseneaza muchiile existente in lista

        for (Edge a : listaMuchii)
        {
            a.drawEdge(g);

        }
        //deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null&& pointEnd!=null){
            g.setColor(Color.GRAY);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
        }

        if(drawConexComponents) {
            visited = new boolean[listaNoduri.size()];
            List<List<Integer>> components = findConnectedComponents();
            for (int i = 0; i < components.size(); i++) {
                Color color = componentColors.get(i);

                for (int nodeIndex : components.get(i)) {
                    Node node = listaNoduri.get(nodeIndex-1);
                    g.setColor(color);
                    node.drawNode(g, node_diam);
                }
            }
            drawConexComponents=false;
        }
else {
            //deseneaza lista de noduri
            for (int i = 0; i < listaNoduri.size(); i++) {
                g.setColor(Color.RED);
                listaNoduri.elementAt(i).drawNode(g, node_diam);
            }
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

