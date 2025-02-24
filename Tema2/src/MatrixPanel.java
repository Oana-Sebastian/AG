import javax.swing.*;
import java.awt.*;

public class MatrixPanel extends JPanel {
    private int[][] matrix;

    public MatrixPanel(int[][] matrix) {
        this.matrix = matrix;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = 20;  // dimensiunea fiecărui pătrat

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                g.setColor(Color.RED);
                g.drawRect((j*size) , (i * size), size, size);
                if (matrix[i][j] == 0) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect((j*size), (i * size), size-1, size-1);
            }
        }
    }
}