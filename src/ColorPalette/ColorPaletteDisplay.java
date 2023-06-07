package ColorPalette;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ColorPaletteDisplay extends JFrame {

    private static Set<Color> getUniqueColors(BufferedImage image) {
        Set<Color> uniqueColors = new HashSet<>();

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                uniqueColors.add(color);
            }
        }

        return uniqueColors;
    }

    private static void displayColorPalette(Set<Color> colors) {

        int paletteSize = colors.size();

        // Calculate the size of the color palette image
        int paletteWidth = paletteSize * 50;
        int paletteHeight = 120;

        // Create a new image to display the color palette
        BufferedImage paletteImage = new BufferedImage(paletteWidth, paletteHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = paletteImage.createGraphics();

        int x = 0;
        int y = 0;

        // Draw each color in the palette
        for (Color color : colors) {
            g2d.setColor(color);
            g2d.fillRect(x, y, 50, paletteHeight);

            // Draw a border around each color rectangle
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, 50, paletteHeight);

            x += 50;

        }

        // Display the color palette image
        displayImage(paletteImage);

        // Cleanup
        g2d.dispose();
    }

    private static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

//        ColorPaletteDisplay palette = new ColorPaletteDisplay();
        String imagePath = "C:/Users/User 2004/Desktop/Color/orange-cat-median-quantized-0.5316230734686083.jpg";

        try {
            File file = new File(imagePath);
            BufferedImage image = ImageIO.read(file);

            // Get unique colors from the image
            Set<Color> uniqueColors = getUniqueColors(image);

            // Display the color palette
            displayColorPalette(uniqueColors);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
