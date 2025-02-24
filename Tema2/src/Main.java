import javax.swing.*;
import java.awt.BorderLayout;

public class Main {


    private static void initUI() {
            JFrame f = new JFrame("Algoritmica Grafurilor");
            //sa se inchida aplicatia atunci cand inchid fereastra
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //imi creez ob MyPanel
            f.add(new MyPanel());
            //setez dimensiunea ferestrei
            f.setSize(1024, 576);
           f.setVisible(true);
            f.setResizable(false);

    }




    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() //new Thread()
        {
            public void run()
            {
                initUI();
            }
        });
    }
}
