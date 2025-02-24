import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import static java.lang.Math.sqrt;

public class Edge
{
    private Node start;
    private Node end;

    public Edge(Node start, Node end)
    {
        this.start = start;
        this.end = end;
    }

    public Point getStart()
    {
        return start.getCoord();
    }

    public Point getEnd()
    {
        return end.getCoord();
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
    public void drawEdge(Graphics g)
    {
        if (start != null &&end!=null) {
            g.drawLine(start.getCoordX(), start.getCoordY(), end.getCoordX(), end.getCoordY());
        }
    }
}

