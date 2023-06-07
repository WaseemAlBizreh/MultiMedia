package ColorHistogram;

import ColorPalette.ColorPaletteDisplay;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;

public class ColorHistogramDisplay extends JFrame {
    private static int[] data;

    public ColorHistogramDisplay() {

    }

    public ColorHistogramDisplay(int[] data) {
        this.data = data;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Histogram Example");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int maxValue = Arrays.stream(data).max().orElse(0);

        int width = getWidth();
        int height = getHeight();
        int barWidth = width / data.length;

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) (((double) data[i] / maxValue) * (height - 20));
            int x = i * barWidth + 10;
            int y = height - barHeight - 10;
            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, barWidth - 2, barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth - 2, barHeight);
        }
    }
}
