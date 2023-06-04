package octree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class OctreeExample {
    public static void main(String[] args) {
        String inputImagePath = "images/orange-cat.jpg";
        String outputImagePath = "images/orange-cat-octree.jpg";
        int maxColors = 256; // Modify the value of maxColors here

        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

            // Apply color quantization using OctreeQuantization algorithm
            BufferedImage outputImage = OctreeQuantization.quantizeImage(inputImage, maxColors);

            // Save the output image
            ImageIO.write(outputImage, "jpg", new File(outputImagePath));

            System.out.println("Image quantization completed successfully.");

        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}