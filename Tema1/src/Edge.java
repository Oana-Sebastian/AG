import java.awt.Graphics;
import java.awt.Point;

public class Edge
{
    private Point start;
    private Point end;

    public Edge(Point start, Point end)
    {
        this.start = start;
        this.end = end;
    }

    public Point getStart()
    {
        return start;
    }

    public Point getEnd()
    {
        return end;
    }

    public void setStart(Point other)
    {
        this.start=other;
    }

    public void setEnd(Point other)
    {
        this.end=other;
    }
    public void drawEdge(Graphics g)
    {
        if (start != null &&end!=null) {
            g.drawLine(start.x, start.y, end.x, end.y);
        }
    }
}

