import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class CTCWindow extends JFrame {
    private List<List<Integer>> ctcComponents;
    private List<List<Integer>> adjCTC;

    public CTCWindow(List<List<Integer>> CTCList, List<List<Integer>> adjCTC) {
        this.ctcComponents = CTCList;
        this.adjCTC = adjCTC;
        setTitle("Componente Tare Conexe");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new CTCPanel(CTCList, adjCTC));
    }

    private class CTCPanel extends JPanel {
        private List<List<Integer>> CTCList;private int[] componentX;
        private int[] componentY;
        private List<List<Integer>> adjCTC;

        private Random random;

        public CTCPanel(List<List<Integer>> components, List<List<Integer>> adjCTC) {
            this.CTCList = components;
            this.adjCTC = adjCTC;
            this.componentX = new int[components.size()];
            this.componentY = new int[components.size()];
            this.random = new Random();


            for (int i = 0; i < components.size(); i++) {
                componentX[i] = random.nextInt(500) + 50;
                componentY[i] = random.nextInt(500) + 50;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int u = 0; u < adjCTC.size(); u++) {
                for (int v : adjCTC.get(u)) {
                    int startX = componentX[u] + 15;
                    int startY = componentY[u] + 15;
                    int endX = componentX[v] + 15;
                    int endY = componentY[v] + 15;
                    int node_diam=30;
                    g.setColor(Color.BLACK);
                    g.drawLine(startX, startY, endX, endY);
                    double v1=Math.sqrt((Math.pow(startX-endX,2))+Math.pow(startY-endY,2));
                    double u1=(startX-endX)/v1;
                    double u2=(startY-endY)/v1;
                    int arrowSize=30;
                    double angle = Math.atan2(endY - startY, endX-startX);
                    int x1 = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
                    int y1 = (int) (endY- arrowSize * Math.sin(angle - Math.PI / 6));
                    int x2 = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
                    int y2 = (int) (endY - arrowSize * Math.sin(angle +Math.PI/6));
                    g.drawLine((int)(endX+ node_diam/2*u1), (int)(endY+ node_diam/2*u2), x1, y1);
                    g.drawLine((int)(endX+ node_diam/2*u1), (int)(endY+ node_diam/2*u2), x2, y2);
                }
            }
            for (int i = 0; i < CTCList.size(); i++) {
                List<Integer> component = CTCList.get(i);
                int x = componentX[i];
                int y = componentY[i];

                g.setColor(Color.GREEN);
                g.fillOval(x, y, 30, 30);
                g.setColor(Color.BLACK);
                g.drawString(component.toString(), x - 2 * component.size(), y + 20);
            }
        }
    }
}