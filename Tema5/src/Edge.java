import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import static java.lang.Math.sqrt;

public class Edge
{
    private Node start;
    private Node end;
    private int weight;
    public Edge(Node start, Node end,int weight)
    {
        this.start = start;
        this.end = end;
        this.weight=weight;
    }

    public int getEndNr()
    {
        return end.getNumber()-1;
    }
    public int getStartNr()
    {
        return start.getNumber()-1;
    }
    public Point getStart()
    {
        return start.getCoord();
    }

    public Point getEnd()
    {
        return end.getCoord();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setStart(Point other)
    {
        this.start.setCoordX(other.x);
        this.start.setCoordY(other.y);
    }

    public void setEnd(Point other)
    {
        this.end.setCoordX(other.x);
        this.end.setCoordY(other.y);
    }
    public void drawEdge(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(start.getCoordX(), start.getCoordY(), end.getCoordX(), end.getCoordY());
        // Calculate and display weight in the middle of the edge
        int midX = (start.getCoordX() + end.getCoordX()) / 2;
        int midY = (start.getCoordY() + end.getCoordY()) / 2;
        g.setColor(Color.BLUE);
        g.drawString(String.valueOf(weight), midX, midY);
    }
}

