package median_cut;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class MedianCutColorQuantizationExample {

    public static void main(String[] args) {
        String inputImagePath = "images/orange-cat.jpg";
        String outputImagePath = "images/orange-cat-medianCut.jpg";

        try {
            // Read the original image
            BufferedImage originalImage = ImageIO.read(new File(inputImagePath));

            // Perform color quantization
            List<Color> quantizedColors = MedianCutColorQuantization.quantizeImage(originalImage, 16);

            // Create a new image with quantized colors
            BufferedImage quantizedImage = createQuantizedImage(originalImage, quantizedColors);

            // Save the quantized image
            File outputFile = new File(outputImagePath);
            ImageIO.write(quantizedImage, "jpg", outputFile);

            System.out.println("Quantized image saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
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
}