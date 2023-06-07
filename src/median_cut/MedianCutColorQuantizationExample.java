package median_cut;

import ColorHistogram.ColorHistogramCalculation;
import ColorHistogram.ColorHistogramDisplay;
import ColorPalette.ColorPaletteDisplay;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MedianCutColorQuantizationExample extends JFrame implements ActionListener {
    JButton button;
    JLabel label;
    JLabel label2;
    JLabel label3;
    JFileChooser fileChooser;
    File selectedFile;

    public MedianCutColorQuantizationExample() {
        setTitle("Median Cut");
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

    @Override
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
                    List<Color> quantizedColors = MedianCutColorQuantization.quantizeImage(inputImage, k);

                    // Create the output image with quantized colors
                    BufferedImage outputImage = createQuantizedImage(inputImage, quantizedColors);

                    // Save the output image
                    File outputFile = new File("images/median-cut-quantized-" + k + ".jpg");
                    ImageIO.write(outputImage, "jpg", outputFile);

                    System.out.println("Output image saved successfully On Your Desktop.");
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

                    ImageIcon iconIndexed = new ImageIcon(indexedImage);
                    label3.setIcon(iconIndexed);
                    label3.setText("Indexed Quantized Image");

                    ColorHistogramCalculation colorHistogramCalculation = new ColorHistogramCalculation();

                    colorHistogramCalculation.getColorHistogram("images/median-cut-quantized-" + k + ".jpg", 256);

                    ColorPaletteDisplay colorPaletteDisplay = new ColorPaletteDisplay();
                    colorPaletteDisplay.getColorPalette("images/median-cut-quantized-" + k + ".jpg");


                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    private static BufferedImage createQuantizedImage(BufferedImage originalImage, List<Color> quantizedColors) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage quantizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = new Color(originalImage.getRGB(x, y));
                Color closestColor = findClosestColor(originalColor, quantizedColors);
                quantizedImage.setRGB(x, y, closestColor.getRGB());
            }
        }

        return quantizedImage;
    }

    private static Color findClosestColor(Color targetColor, List<Color> colorPalette) {
        Color closestColor = colorPalette.get(0);
        double closestDistance = calculateDistance(targetColor, closestColor);

        for (Color color : colorPalette) {
            double distance = calculateDistance(targetColor, color);
            if (distance < closestDistance) {
                closestColor = color;
                closestDistance = distance;
            }
        }

        return closestColor;
    }

    private static double calculateDistance(Color color1, Color color2) {
        int rDiff = color1.getRed() - color2.getRed();
        int gDiff = color1.getGreen() - color2.getGreen();
        int bDiff = color1.getBlue() - color2.getBlue();

        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }

    public static void main(String[] args) {
        try {
            MedianCutColorQuantizationExample uploader = new MedianCutColorQuantizationExample();

            uploader.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}