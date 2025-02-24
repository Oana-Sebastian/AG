import javax.swing.*;
import javax.swing.Timer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.util.List;

public class MyPanel extends JPanel {
    private int[][] matrix;
    private List<List<Integer>> adjMatrix;
    private Vector<Point> listaNoduri;
    private Vector<Point> listaArce;
    private int lines;
    private int cols;
    private int startX;
    private int startY;
    private int squareSize = 100;
    private Vector<Vector<Integer>> listaCai;
    private boolean[] visited;
    private int currentPathIndex = 0; // Tracks the current path being displayed
    private int delay = 2000; // Delay between commands in milliseconds
    private Timer commandTimer;
    private void readMatrixFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String primaLinie = br.readLine();
            String[] dimensiuni = primaLinie.trim().split("\\s+");
            lines = Integer.parseInt(dimensiuni[0]);
            cols = Integer.parseInt(dimensiuni[1]);

            matrix = new int[lines][cols];

            for (int i = 0; i < lines; i++) {
                String linie = br.readLine();
                String[] valori = linie.trim().split("\\s+");

                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = Integer.parseInt(valori[j]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    static void addEdge(List<List<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    void createAdjMatrix() {
        adjMatrix = new ArrayList<>(listaNoduri.size());
        for (int i = 0; i < listaNoduri.size(); i++) {
            adjMatrix.add(new ArrayList<>());
        }

        for (Point p : listaArce) {
            addEdge(adjMatrix, p.x - 1, p.y - 1);
        }

        System.out.println("Adjacency List:");
        int i = 1;
        for (List<Integer> list : adjMatrix) {
            System.out.print(i + ": ");
            for (Integer elem : list) {
                System.out.print((elem + 1) + " ");
            }
            i++;
            System.out.println();
        }
    }
    public void incrementalBFSPaths(List<List<Integer>> adj, int start) {


        Queue<List<Integer>> queue = new LinkedList<>();
        int startNode = listaNoduri.indexOf(new Point(startX, startY));
        queue.add(new ArrayList<>(List.of(startNode))); // Add start node as the initial path

        while (!queue.isEmpty()) {
            List<Integer> currentPath = queue.poll();
            int currentNode = currentPath.get(currentPath.size() - 1);

            // Check if the current node is on the matrix edge
            Point nodeCoords = listaNoduri.get(currentNode);
            if (nodeCoords.x == 0 || nodeCoords.y == 0 || nodeCoords.x == cols - 1 || nodeCoords.y == lines - 1) {
                // Add path to resultPaths if it reaches an edge
                Vector<Integer> pathToEdge = new Vector<>(currentPath);
                if(pathToEdge.size()!=1) {
                    if (pathToEdge.get(0) != pathToEdge.get(pathToEdge.size() - 1))
                        listaCai.add(pathToEdge);
                }else listaCai.add(pathToEdge);
                System.out.println("Edge path: " + pathToEdge);
            }

            // Enqueue all adjacent nodes
            for (int neighbor : adjMatrix.get(currentNode)) {
                if (!visited[neighbor]) {
                    List<Integer> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                    visited[neighbor]=true;
                }
            }

        }

    }

    void startCoordinates() {
        String lineInput = JOptionPane.showInputDialog("Introduceți coordonata pentru linie:");
        String columnInput = JOptionPane.showInputDialog("Introduceți coordonata pentru coloană:");

        if (lineInput != null && columnInput != null) {
            try {
                startY = Integer.parseInt(lineInput) - 1;
                startX = Integer.parseInt(columnInput) - 1;
                System.out.println("Coordonata selectată: (" + (startX + 1) + ", " + (startY + 1) + ")");
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numeric values.");
            }
        } else {
            System.out.println("Input canceled.");
        }
    }

    public void createNodesAndArcs() {


        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    Point node = new Point(j, i);
                    listaNoduri.add(node);

                    if (i > 0 && matrix[i - 1][j] == 1) {
                        Point vecin = new Point(j, i - 1);
                        listaArce.add(new Point(listaNoduri.indexOf(node) + 1, listaNoduri.indexOf(vecin) + 1));
                    }
                    if (j > 0 && matrix[i][j - 1] == 1) {
                        Point vecin = new Point(j - 1, i);
                        listaArce.add(new Point(listaNoduri.indexOf(node) + 1, listaNoduri.indexOf(vecin) + 1));
                    }
                }
            }
        }
    }

    public MyPanel() {
        readMatrixFromFile("matrice.txt");
        setBorder(BorderFactory.createLineBorder(Color.black));
        startCoordinates();

        if (startY < 0 || startY >= lines || startX < 0 || startX >= cols) {
            System.out.println("Start coordinates are out of matrix bounds.");
            return;
        }
        listaNoduri = new Vector<>();
        listaArce = new Vector<>();
        createNodesAndArcs();
        createAdjMatrix();
        listaCai=new Vector<>();
        int startNodeIndex = listaNoduri.indexOf(new Point(startX, startY));
        if (startNodeIndex == -1) {
            System.out.println("The start node does not exist.");
            return;
        }
        System.out.println("BFS starting from " + (startNodeIndex + 1));

        visited=new boolean[listaNoduri.size()];
        incrementalBFSPaths(adjMatrix,startNodeIndex);
        while(listaCai.size()*delay>30000) {
            delay -= 100;
            System.out.println(listaCai.size() * delay);
        }
         commandTimer = new Timer(delay, e -> {
            if (currentPathIndex<listaCai.size()) {
                repaint(); // Trigger repaint to show the current path
                currentPathIndex++;
                currentPathIndex%=listaCai.size();
            } else {
                commandTimer.stop(); // Stop the timer when all paths are processed
            }
        });
        commandTimer.start(); // Start the timer
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (startX < cols && startY < lines){
        while (lines * squareSize > 500) {
            squareSize -= 10;
        }
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < cols; j++) {
                int nodeIndex=listaNoduri.indexOf(new Point(j,i));
                if (matrix[i][j] == 1) {
                    if(visited!=null)
                        if(nodeIndex!=-1&&!visited[nodeIndex])
                            g.setColor(Color.RED);
                        else
                            g.setColor(Color.WHITE);
                }
                else
                    g.setColor(Color.BLACK);
                g.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);

                    if (matrix[startY][startX] == 1) {
                        g.setColor(Color.BLUE);
                        if(listaCai.isEmpty())
                        {
                            g.setColor(Color.CYAN);

                        }
                    } else {
                        g.setColor(Color.MAGENTA);
                    }
                    g.fillRect(startX * squareSize, startY * squareSize, squareSize, squareSize);

                g.setColor(Color.DARK_GRAY);



            }
        }
        if (currentPathIndex < listaCai.size()) {
            int startNodeIndex=listaNoduri.indexOf(new Point(startX,startY));
            g.setColor(Color.GREEN);
            Vector<Integer> pathToDisplay = listaCai.get(currentPathIndex);

            for (Integer nodeIndex : pathToDisplay) {
                if(nodeIndex==startNodeIndex && pathToDisplay.size()>1)
                {
                    g.setColor(Color.BLUE);
                    Point nodeCoord = listaNoduri.get(nodeIndex);
                    g.fillRect(nodeCoord.x * squareSize, nodeCoord.y * squareSize, squareSize, squareSize);
                }
                else
                    g.setColor(Color.GREEN);
                Point nodeCoord = listaNoduri.get(nodeIndex);
                g.fillRect(nodeCoord.x * squareSize, nodeCoord.y * squareSize, squareSize, squareSize);
            }
        }
        }
    }
}

