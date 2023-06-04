package Uniform;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UniformColorQuantization {

    public static void main(String[] args) {
        String imagePath = "images/orange-cat.jpg";
        int numColors = 3;

        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            BufferedImage quantizedImage = quantize(originalImage, numColors);

            // Save the quantized image
            String outputImagePath = "images/orange-cat-uniform.jpg";
            ImageIO.write(quantizedImage, "jpg", new File(outputImagePath));

            System.out.println("Quantization complete. Quantized image saved at: " + outputImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage quantize(BufferedImage originalImage, int numColors) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a new BufferedImage for the quantized image
        BufferedImage quantizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Calculate the color range for each channel
        int colorRange = 256 / numColors;

        // Iterate over each pixel in the original image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB values of the pixel
                int rgb = originalImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Quantize the colors
                int quantizedRed = (red / colorRange) * colorRange;
                int quantizedGreen = (green / colorRange) * colorRange;
                int quantizedBlue = (blue / colorRange) * colorRange;

                // Create the quantized color
                Color quantizedColor = new Color(quantizedRed, quantizedGreen, quantizedBlue);

                // Set the quantized color in the new image
                quantizedImage.setRGB(x, y, quantizedColor.getRGB());
            }
        }

        return quantizedImage;
    }
}