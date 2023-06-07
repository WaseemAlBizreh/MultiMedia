package ColorHistogram;

import ColorPalette.ColorPaletteDisplay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ColorHistogramCalculation extends ColorHistogramDisplay{

    public ColorHistogramCalculation(int[] histogram) {
        super(histogram);
    }

    public static int[] calculateColorHistogram(String imagePath, int numBins) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int[] histogram = new int[numBins];

        // Calculate histogram
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                int red = pixelColor.getRed();
                int green = pixelColor.getGreen();
                int blue = pixelColor.getBlue();

                // Calculate the bin index based on the color intensity range
                int binIndex = (red * numBins) / 256;

                // Increment the histogram count for the corresponding bin
                histogram[binIndex]++;
            }
        }

        return histogram;
    }

    public static void main(String[] args) {
        String imagePath = "C:/Users/User 2004/Desktop/Color/orange-cat-median-quantized-17.jpg";
        int numBins = 256; // Number of bins in the histogram

        try {
            int[] histogram = calculateColorHistogram(imagePath, numBins);

            // Print the histogram values
            for (int i = 0; i < histogram.length; i++) {
                System.out.println("Bin " + i + ": " + histogram[i]);
            }

            SwingUtilities.invokeLater(() -> new ColorHistogramDisplay(histogram));

            ColorPaletteDisplay palette = new ColorPaletteDisplay();
            //palette.main(args); for display histogram and palette


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
