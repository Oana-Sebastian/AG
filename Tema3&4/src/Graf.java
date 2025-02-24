import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
public class Graf
{
    private static void initUI() {
        JFrame f = new JFrame("Algoritmica Grafurilor- Tema 3");
        //sa se inchida aplicatia atunci cand inchid fereastra
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //imi creez ob MyPanel
        f.add(new MyPanel());
        //setez dimensiunea ferestrei
        f.setSize(1024,576);
        // Create buttons
        f.setLayout(new BorderLayout());

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        JButton directedGraphButton = new JButton("Directed Graph");
        JButton undirectedGraphButton = new JButton("Undirected Graph");

        // Add action listener for directed graph button
        directedGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the directed graph panel
                MyPanel myPanel = new MyPanel();
                f.setContentPane(myPanel);
                f.revalidate(); // Refresh the frame
            }
        });

        // Add action listener for undirected graph button
        undirectedGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyPanelUnoriented myPanelUnoriented=new MyPanelUnoriented();
                f.setContentPane(myPanelUnoriented);
                f.revalidate();
            }
        });

        buttonPanel.add(directedGraphButton);
       buttonPanel.add(undirectedGraphButton);
        f.add(buttonPanel, BorderLayout.CENTER);
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