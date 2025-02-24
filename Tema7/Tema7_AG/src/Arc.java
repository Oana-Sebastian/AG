import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import static java.lang.Math.sqrt;

public class Arc
{
    private Node start;
    private Node end;
    private int capacity;
    private int flow;

    public Arc(Node start, Node end,int capacity,int flow)
    {
        this.start = start;
        this.end = end;
        this.capacity = capacity;
        this.flow=flow;
    }


    public Node getStartNode() {
        return start;
    }

    public Node getEndNode() {
        return end;
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

    public int getCapacity() {
        return capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public void drawArc(Graphics g)
    {
        if (start != null &&end!=null)
        {
            int NODE_DIAM=30;
//            g.setColor(Color.BLACK);
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
            g.drawLine((int)(end.getCoordX()+ (float)NODE_DIAM/2*u1), (int)(end.getCoordY()+ (float)NODE_DIAM/2*u2), x1, y1);
            g.drawLine((int)(end.getCoordX()+ (float)NODE_DIAM/2*u1), (int)(end.getCoordY()+ (float)NODE_DIAM/2*u2), x2, y2);


            int midX = (start.getCoordX() + end.getCoordX()) / 2;
            int midY = (start.getCoordY() + end.getCoordY()) / 2;

            int dx = end.getCoordX() - start.getCoordX();
            int dy = end.getCoordY() - start.getCoordY();


            double distance = sqrt(dx * dx + dy * dy);
            int offsetX = (int) (dx / distance * 20);  // Offset de 20px pe axa X
            int offsetY = (int) (dy / distance * 20);  // Offset de 20px pe axa Y


            int textX = midX - offsetX;
            int textY = midY - offsetY;

            g.setColor(Color.BLUE);
            g.drawString(flow + "/" + capacity, textX, textY);
        }


    }
}