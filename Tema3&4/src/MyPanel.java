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
import java.util.List;


public class MyPanel extends JPanel {
    private Node draggedNode = null;
    private int nodeNr = 1;
    private int node_diam = 30;
    private Vector<Node> listaNoduri;
    private Vector<Arc> listaArce;
    private int max_nodes = 100;
    private int[][] adjMatrix;
    Point pointStart = null;
    Point pointEnd = null;
    boolean isDragging = false;
    boolean rightDragging = false;
    private JButton topologicalSort;
    private JButton arborescentCheck;
    private JButton stronglyConex;
    private List<List<Integer>> adjList=new ArrayList<>();
    private List<List<Integer>> adjListTransposed=new ArrayList<>();
    private int treeRootNr;
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
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    private double distance(Point coord1, Point coord2) {
        return sqrt((coord2.x - coord1.x) * (coord2.x - coord1.x) + (coord2.y - coord1.y) * (coord2.y - coord1.y));
    }



    public MyPanel() {
        listaNoduri = new Vector<Node>();
        listaArce = new Vector<Arc>();
        adjMatrix = new int[max_nodes][max_nodes];
        writeAdjMatrixToFile("matrix.txt");

        topologicalSort = new JButton("Topological Sort");
        topologicalSort.addActionListener(e -> {
            List<Integer> result = topologicalSort();
            if (result != null) {
                System.out.println("Topological Order: " + result);
                JOptionPane.showMessageDialog(this, "Topological Order: " + result);
            } else {
                JOptionPane.showMessageDialog(this, "Graful are un ciclu si nu poate fi sortat topologic");
            }
        });
        this.add(topologicalSort);

        arborescentCheck=new JButton("Arborescenta");
        arborescentCheck.addActionListener(e->{
            if(isArborescent())
            {
                JOptionPane.showMessageDialog(this,"Nodul radacina este "+ treeRootNr);
                repaint();
            }
            else JOptionPane.showMessageDialog(this,"Graful nu este arborescent");
        });
        this.add(arborescentCheck);


        stronglyConex = new JButton("Componente Tare Conexe");
        stronglyConex.addActionListener(e -> {
            List<List<Integer>> components = CTC();
            generateRandomColors(components.size());
            drawConexComponents = true;
            repaint();

            StringBuilder result = new StringBuilder("Componentele tare conexe sunt:\n");
            for (List<Integer> component : components) {
                result.append(component).append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString(), "Componente Conexe", JOptionPane.INFORMATION_MESSAGE);

            System.out.println("Lista adiacenta");
            for (int i = 0; i < listaNoduri.size(); i++) {
                System.out.print((i + 1) + ":");
                for (int j = 0; j < adjList.get(i).size(); j++)
                    System.out.print((adjList.get(i).get(j)) + " ");
                System.out.println();
            }

            List<List<Integer>> adjCTC = new ArrayList<>();
            Map<Integer, Integer> nodeToCTC = new HashMap<>();

            for (int i = 0; i < components.size(); i++) {
                for (Integer node : components.get(i)) {
                    nodeToCTC.put(node - 1, i); // Map original node to CTC index
                }
                adjCTC.add(new ArrayList<>());
            }

            System.out.println("Node to CTC Map: " + nodeToCTC);

            for (int u = 0; u < adjList.size(); u++) {
                for (int v : adjList.get(u)) {
                    Integer ctcU = nodeToCTC.get(u);
                    Integer ctcV = nodeToCTC.get(v - 1);

                    if (ctcU != null && ctcV != null && ctcU != ctcV && !adjCTC.get(ctcU).contains(ctcV)) {
                        adjCTC.get(ctcU).add(ctcV);
                        System.out.println(ctcU + " -> " + ctcV);
                    }
                }
            }

            CTCWindow ctcWindow = new CTCWindow(components,adjCTC);
            ctcWindow.setVisible(true);
            ctcWindow.setResizable(false);
        });
        this.add(stronglyConex);

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
                                Arc arc = new Arc(firstNode, secondNode);
                                listaArce.add(arc);
                                adjList.get(firstNode.getNumber()-1).add(secondNode.getNumber());
                                adjListTransposed.get(secondNode.getNumber()-1).add(firstNode.getNumber());
                                adjMatrix[firstNode.getNumber()-1][secondNode.getNumber()-1] = 1;
                                //adjMatrix[secondNode.getNumber() - 1][firstNode.getNumber() - 1] =1;
                                writeAdjMatrixToFile("matrix.txt");
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

    private void generateRandomColors(int numComponents) {
        componentColors = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numComponents; i++) {
            componentColors.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
    }

    private boolean dfsCheck(int startNode) {
        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);
        boolean[] visited= new boolean[listaNoduri.size()];
        visited[startNode-1] = true;
        int cnt=1;
        while (!stack.isEmpty()) {
            int node = stack.pop();

            for (int neighbor : adjList.get(node-1)) {
                if (!visited[neighbor-1]) {
                    visited[neighbor-1] = true;
                    cnt++;
                    stack.push(neighbor);
                }
            }
        }
        return cnt == listaNoduri.size();
    }

    private void fillOrder(int node, boolean[] visited, Stack<Integer> stack,List<List<Integer>> adj) {
        Stack<Integer> dfsStack = new Stack<>();
        dfsStack.push(node);

        while (!dfsStack.isEmpty()) {
            int currentNode = dfsStack.peek();
            if (!visited[currentNode-1]) {
                visited[currentNode-1] = true;
                for (int neighbor : adj.get(currentNode-1)) {
                    if (!visited[neighbor-1])
                        dfsStack.push(neighbor);
                }
            } else {
                dfsStack.pop();
                if (!stack.contains(currentNode))
                    stack.push(currentNode);
            }
        }
    }

    private void dfs(int node, boolean[] visited, List<Integer> component,List<List<Integer>> adj) {
        Stack<Integer> dfsStack = new Stack<>();
        dfsStack.push(node);

        while (!dfsStack.isEmpty()) {
            int currentNode = dfsStack.pop();
            if (!visited[currentNode-1]) {
                visited[currentNode-1] = true;
                component.add(currentNode);
                for (int neighbor : adj.get(currentNode-1)) {
                    if (!visited[neighbor-1])
                        dfsStack.push(neighbor);
                }
            }
        }
    }

    public List<List<Integer>> CTC() {
        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[listaNoduri.size()];


        for (int i = 0; i <listaNoduri.size(); i++)
            if (!visited[i])
                fillOrder(i+1, visited, stack,adjList);

        Arrays.fill(visited, false);
        List<List<Integer>> ctcComponents = new ArrayList<>();
        while (!stack.isEmpty()) {
            int node = stack.pop();
            if (!visited[node-1]) {
                List<Integer> component = new ArrayList<>();
                dfs(node,visited,component,adjListTransposed);
                ctcComponents.add(component);
            }
        }
        return ctcComponents;
    }


    private boolean isArborescent()
    {
        treeRootNr=-1;
        if(listaArce.size()!=listaNoduri.size()-1)
            return false;
        int [] indegree=new int[listaNoduri.size()];

        for(int i=0;i<listaNoduri.size();i++)
        {

            for(int j=0;j<adjList.get(i).size();j++)
            {
                indegree[adjList.get(i).get(j)-1]++;

            }
        }
        for(int i = 0; i< adjList.size(); i++) {
            System.out.print((i + 1) + ":");
            for (int j = 0; j < adjList.get(i).size(); j++) {
                System.out.print(adjList.get(i).get(j) + " ");
            }
            System.out.println();
        }
        for(int i=0;i<listaNoduri.size();i++)
        {
            if(indegree[i]==0)
            {if(treeRootNr==-1)
                treeRootNr=i+1;
                else
                    return false;
            }
            else
            if(indegree[i]>1) {
                return false;
            }

        }
        if(treeRootNr==-1)
            return false;

        return dfsCheck(treeRootNr);
    }

    public List<Integer> topologicalSort() {
        int[] indegree = new int[listaNoduri.size()];


        for(int i = 0; i< adjList.size(); i++)
        {
            System.out.print((i+1) + ":");
            for(int j = 0; j< adjList.get(i).size(); j++)
            {
                System.out.print(adjList.get(i).get(j) + " ");
            }
            System.out.println();
        }


        for (int i = 0; i < listaNoduri.size(); i++) {
            for (int neighbor : adjList.get(i)) {
                indegree[neighbor-1]++;

            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (Node node:listaNoduri) {
            if (indegree[node.getNumber()-1] == 0){
                    queue.add(node.getNumber());
            }
        }

        List<Integer> topologicalOrder = new ArrayList<>();

        while (!queue.isEmpty()) {

            int node = queue.poll();
            topologicalOrder.add(node);

            for (int neighbor : adjList.get(node-1)) {
                indegree[neighbor-1]--;

              if (indegree[neighbor-1] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (topologicalOrder.size() != listaNoduri.size()) {
            System.out.println("Exista un ciclu in graf");
            System.out.println(topologicalOrder.size());
            return null;
        }
        return topologicalOrder;
    }
    //metoda care se apeleaza la eliberarea mouse-ului
    private void addNode(int x, int y) {
        if (x > 1024 - node_diam - 10)
            x = 1024 - node_diam - 11;
        if (x < node_diam)
            x = node_diam;
        if (y > 571 - 2 * node_diam)
            y = 570 - 2 * node_diam;
        if (y < node_diam + 10)
            y = node_diam;
        boolean isDist = true;
        for (int i = 0; i < listaNoduri.size(); i++) {
            int node_coord_x = listaNoduri.elementAt(i).getCoordX();
            int node_coord_y = listaNoduri.elementAt(i).getCoordY();
            if (sqrt((node_coord_x - x) * (node_coord_x - x) + (node_coord_y - y) * (node_coord_y - y)) < 2 * node_diam)
                isDist = false;
        }
        if (isDist) {
            Node node = new Node(x, y, nodeNr);
            listaNoduri.add(node);
            adjList.add(new ArrayList<>());
            adjListTransposed.add(new ArrayList<>());
            nodeNr++;
            repaint();
        }
    }
    //se executa atunci cand apelam repaint()

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);//apelez metoda paintComponent din clasa de baza
        g.drawString("This is my Graph!", 10, 20);
        g.drawString("Numar noduri: " + listaNoduri.size(), 10, 40);
        g.drawString("Numar arce: " + listaArce.size(), 10, 60);
        //deseneaza arcele existente in lista
		/*for(int i=0;i<listaArce.size();i++)
		{
			listaArce.elementAt(i).drawArc(g);
		}*/


        for (Arc a : listaArce) {
            a.drawArc(g);
        }

        //deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null && pointEnd != null) {
            g.setColor(Color.GRAY);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
            double v1 = Math.sqrt((Math.pow(pointStart.x - pointEnd.x, 2)) + Math.pow(pointStart.y - pointEnd.y, 2));
            double u1 = (pointStart.x - pointEnd.x) / v1;
            double u2 = (pointStart.y - pointEnd.y) / v1;
            int arrowSize = 30;
            double angle = Math.atan2(pointEnd.y - pointStart.y, pointEnd.x - pointStart.x);
            int x1 = (int) (pointEnd.x - arrowSize * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (pointEnd.y - arrowSize * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (pointEnd.x - arrowSize * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (pointEnd.y - arrowSize * Math.sin(angle + Math.PI / 6));
            g.drawLine((int) (pointEnd.x + (node_diam) / 2 * u1), (int) (pointEnd.y + (node_diam) / 2 * u2), x1, y1);
            g.drawLine((int) (pointEnd.x + (node_diam) / 2 * u1), (int) (pointEnd.y + (node_diam) / 2 * u2), x2, y2);
            //g.drawLine(x1,y1,x2,y2);
        }

        if(drawConexComponents)
        {
            List<List<Integer>> components=CTC();
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
                if (treeRootNr != -1 && listaNoduri.elementAt(i).getNumber() == treeRootNr) {
                    g.setColor(Color.BLUE);
                } else
                    g.setColor(Color.RED);
                listaNoduri.elementAt(i).drawNode(g, node_diam);

            }
        }
		/*for (Node nod : listaNoduri)
		{
			nod.drawNode(g, node_diam, node_Diam);
		}*/
    }

    private Node getNodeAtPoint(Point p) {
        for (Node node : listaNoduri) {
            if (distance(p, node.getCoord()) <= node_diam / 2.0) {
                return node;
            }
        }
        return null;
    }

    private void updateArcsForNode(Node node) {
        for (Arc arc : listaArce) {
            if (distance(arc.getStart(), node.getCoord()) <=1.2* node_diam) {
                arc.setStart(node.getCoord());
            } else if (distance(arc.getEnd(), node.getCoord()) <=1.2* node_diam) {
                arc.setEnd(node.getCoord());
            }
        }
    }
}

