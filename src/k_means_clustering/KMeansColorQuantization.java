package k_means_clustering;

import ColorHistogram.ColorHistogramCalculation;
import ColorPalette.ColorPaletteDisplay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class KMeansColorQuantization extends JFrame implements ActionListener {
    JButton button;
    JLabel label;
    JLabel label2;
    JLabel label3;
    JFileChooser fileChooser;
    File selectedFile;

    public KMeansColorQuantization() {
        setTitle("K-means Clistering");
        setSize(300, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        label = new JLabel("No file selected");
        button = new JButton("Select file");
        button.addActionListener(this);

        label2 = new JLabel("");
        label3 = new JLabel("");

        add(button);
        add(label);
        add(label2);
        add(label3);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            fileChooser = new JFileChooser();
            File defaultDirectory = new File("C:/Users/Dell/Desktop");
            fileChooser.setCurrentDirectory(defaultDirectory);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage inputImage = ImageIO.read(selectedFile);

                    // Define the number of colors for quantization
                    int k = 16;

                    // Perform color quantization
                    List<Color> quantizedColors = KMeansClustering.performColorQuantization(inputImage, k);

                    // Create the output image with quantized colors
                    BufferedImage outputImage = createOutputImage(inputImage, quantizedColors);

                    // Save the output image
                    File outputFile = new File("images/k-cluster-quantized-" + k + ".jpg");
                    ImageIO.write(outputImage, "jpg", outputFile);

                    System.out.println("Output image saved successfully In Your Images Folder.");
                    ImageIcon icon = new ImageIcon(inputImage);
                    label.setIcon(icon);
                    label.setText("Original Image");

                    ImageIcon iconOutput = new ImageIcon(outputImage);
                    label2.setIcon(iconOutput);
                    label2.setText("Quantized Image");

                    // Indexed Image
                    BufferedImage indexedImage = new BufferedImage(outputImage.getWidth(), outputImage.getHeight(),
                            BufferedImage.TYPE_BYTE_INDEXED);
                    Graphics g = indexedImage.getGraphics();
                    g.drawImage(outputImage, 0, 0, null);
                    g.dispose();

                    File indexedFile = new File("images/k-cluster-quantized-indexed-" + k + ".jpg");
                    ImageIO.write(indexedImage, "jpg", indexedFile);

                    ImageIcon iconIndexed = new ImageIcon(indexedImage);
                    label3.setIcon(iconIndexed);
                    label3.setText("Indexed Quantized Image");

                    ColorHistogramCalculation colorHistogramCalculation = new ColorHistogramCalculation();

                    colorHistogramCalculation.getColorHistogram("images/k-cluster-quantized-" + k + ".jpg", 256);

                    ColorPaletteDisplay colorPaletteDisplay = new ColorPaletteDisplay();
                    colorPaletteDisplay.getColorPalette("images/k-cluster-quantized-" + k + ".jpg");


                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    private static BufferedImage createOutputImage(BufferedImage inputImage, List<Color> quantizedColors) {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // Replace each pixel color in the output image with the nearest quantized color
        for (int y = 0; y < inputImage.getHeight(); y++) {
            for (int x = 0; x < inputImage.getWidth(); x++) {
                Color originalColor = new Color(inputImage.getRGB(x, y), true);
                Color nearestColor = getNearestColor(originalColor, quantizedColors);
                outputImage.setRGB(x, y, nearestColor.getRGB());
            }
        }

        return outputImage;
    }

    private static Color getNearestColor(Color color, List<Color> quantizedColors) {
        double minDistance = Double.MAX_VALUE;
        Color nearestColor = quantizedColors.get(0);

        for (Color quantizedColor : quantizedColors) {
            double distance = calculateDistance(color, quantizedColor);
            if (distance < minDistance) {
                minDistance = distance;
                nearestColor = quantizedColor;
            }
        }

        return nearestColor;
    }

    private static double calculateDistance(Color color1, Color color2) {
        int redDiff = color1.getRed() - color2.getRed();
        int greenDiff = color1.getGreen() - color2.getGreen();
        int blueDiff = color1.getBlue() - color2.getBlue();

        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    public static void main(String[] args) {
        try {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            KMeansColorQuantization uploader = new KMeansColorQuantization();
            uploader.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}