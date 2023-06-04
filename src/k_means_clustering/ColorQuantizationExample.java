package k_means_clustering;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ColorQuantizationExample {

    public static void main(String[] args) {
        try {
            String imageName = "orange-cat.jpg";
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File("images/" + imageName));

            // Define the number of colors for quantization
            int k = 16;

            // Perform color quantization
            List<Color> quantizedColors = KMeansColorQuantization.performColorQuantization(inputImage, k);

            // Create the output image with quantized colors
            BufferedImage outputImage = createOutputImage(inputImage, quantizedColors);

            // Save the output image
            File outputFile = new File("images/k_" + k + "_" + imageName);
            ImageIO.write(outputImage, "jpg", outputFile);

            System.out.println("Output image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createOutputImage(BufferedImage inputImage, List<Color> quantizedColors) {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);

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
}