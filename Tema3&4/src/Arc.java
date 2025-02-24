import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import static java.lang.Math.sqrt;

public class Arc
{
    private Node start;
    private Node end;

    public Arc(Node start, Node end)
    {
        this.start = start;
        this.end = end;
    }

    public Point getStart()
    {
        return start.getCoord();
    }

    public int getStartNumber()
    {
        return start.getNumber();
    }
    public int getEndNumber()
    {
        return end.getNumber();
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
    public void drawArc(Graphics g)
    {
        if (start != null &&end!=null)
        {
            int node_diam=30;
            g.setColor(Color.BLACK);
            g.drawLine(start.getCoordX(), start.getCoordY(), end.getCoordX(), end.getCoordY());
            double v1=Math.sqrt((Math.pow(start.getCoordX()-end.getCoordX(),2))+Math.pow(start.getCoordY()-end.getCoordY(),2));
            double u1=(start.getCoordX()-end.getCoordX())/v1;
            double u2=(start.getCoordY()-end.getCoordY())/v1;
            int arrowSize=30;
            double angle = Math.atan2(end.getCoordY() - start.getCoordY(), end.getCoordX()-start.getCoordX());
            int x1 = (int) (end.getCoordX() - arrowSize * Math.cos(angle - Math.PI / 6));
            int y1 = (int) (end.getCoordY() - arrowSize * Math.sin(angle - Math.PI / 6));
            int x2 = (int) (end.getCoordX() - arrowSize * Math.cos(angle + Math.PI / 6));
            int y2 = (int) (end.getCoordY() - arrowSize * Math.sin(angle +Math.PI/6));
            g.drawLine((int)(end.getCoordX()+ node_diam/2*u1), (int)(end.getCoordY()+ node_diam/2*u2), x1, y1);
            g.drawLine((int)(end.getCoordX()+ node_diam/2*u1), (int)(end.getCoordY()+ node_diam/2*u2), x2, y2);

        }

    }
}

