package search;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageSearcher {
    public static void main(String[] args) {
        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File("C:\\Users\\User 2004\\Desktop\\Color\\orange-cat-median-quantized-17.jpg"));

            // Calculate the histogram for the input image
            int[] inputHistogram = calculateHistogram(inputImage);

            // Specify the folder to search for similar images
            String folderPath = "C:\\Users\\User 2004\\Desktop\\Color";

            // Set the similarity threshold to display similar images
            double similarityThreshold = 0.7;  //change between 0 and 1

            // Iterate over the images in the folder
            File folder = new File(folderPath);
            File[] imageFiles = folder.listFiles();

            for (File file : imageFiles) {
                if (file.isFile()) {
                    // Load the image from the file
                    BufferedImage image = ImageIO.read(file);

                    // Calculate the histogram for the current image
                    int[] histogram = calculateHistogram(image);

                    // Compare the histograms using histogram intersection
                    double intersectionScore = compareHistogramsIntersection(inputHistogram, histogram);

                    // Display the image if the similarity score is above the threshold
                    if (intersectionScore >= similarityThreshold) {
                        displayImage(image, "Similarity Score: " + intersectionScore);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] calculateHistogram(BufferedImage image) {
        int[] histogram = new int[256];
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                int intensity = pixelColor.getRed(); // Use getRed() for grayscale or red channel value

                histogram[intensity]++;
            }
        }

        return histogram;
    }

    private static double compareHistogramsIntersection(int[] histogram1, int[] histogram2) {
        int intersection = 0;

        for (int i = 0; i < 256; i++) {
            intersection += Math.min(histogram1[i], histogram2[i]);
        }

        double totalPixels = histogram1.length; // Assuming both histograms have the same size

        return intersection / totalPixels;
    }

    private static void displayImage(BufferedImage image, String title) {
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        JFrame frame = new JFrame(title);
        frame.getContentPane().add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
