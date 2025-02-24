import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class Node
{
    private int coordX;
    private int coordY;
    private int number;

    public Node(int coordX, int coordY, int number)
    {
        this.coordX = coordX;
        this.coordY = coordY;
        this.number = number;
    }



    public int getCoordX() {
        return coordX;
    }
    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }
    public int getCoordY() {
        return coordY;
    }
    public Point getCoord(){
        return new Point(coordX,coordY);
    }
    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }
    public int getNumber() {
        return number;
    }

    public void drawNode(Graphics g, int node_diam)
    {

       // g.setColor(Color.RED);
        g.setFont(new Font("TimesRoman", Font.BOLD, 15));
        g.fillOval(coordX-node_diam/2, coordY-node_diam/2, node_diam, node_diam);
        g.setColor(Color.BLACK);
        g.drawOval(coordX-node_diam/2, coordY-node_diam/2, node_diam, node_diam);
        g.setColor(Color.WHITE);
        if(number < 10)
            g.drawString(((Integer)number).toString(), coordX+11-node_diam/2, coordY+20-node_diam/2);
        else
            g.drawString(((Integer)number).toString(), coordX+6-node_diam/2, coordY+20-node_diam/2);
    }
}
