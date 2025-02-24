import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
public class Graf
{
    private static void initUI() {
        JFrame f = new JFrame("Algoritmica Grafurilor");
        //sa se inchida aplicatia atunci cand inchid fereastra
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //imi creez ob MyPanel
        f.add(new MyPanel());
        //setez dimensiunea ferestrei
        f.setSize(1024,576);
        // Create button
        f.setResizable(false);
        // Make the frame visible
        f.setVisible(true);
    }


    public static void main(String[] args)
    {
        //pornesc firul de executie grafic
        //fie prin implementarea interfetei Runnable, fie printr-un ob al clasei Thread
        SwingUtilities.invokeLater(new Runnable() //new Thread()
        {
            public void run()
            {
                initUI();
            }
        });
    }
}