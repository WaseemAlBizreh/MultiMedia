package search;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageSizeSearch {
    public static void main(String[] args) {
        String inputImagePath = "images/image4.jpg";
        String folderPath = "images/";

        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

            // Get the size of the input image
            Dimension inputSize = new Dimension(inputImage.getWidth(), inputImage.getHeight());

            // Search for similar images in size
            List<String> similarImages = searchSimilarImagesBySize(folderPath, inputSize);

            // Display the similar images
            System.out.println("Similar images in size:");
            for (String image : similarImages) {
                System.out.println(image);
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    public static List<String> searchSimilarImagesBySize(String folderPath, Dimension targetSize) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<String> similarImages = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    // Load the image
                    BufferedImage image = ImageIO.read(file);

                    // Get the size of the image
                    Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

                    // Compare the sizes
                    if (imageSize.width == targetSize.width && imageSize.height == targetSize.height) {
                        similarImages.add(file.getName());
                    }
                } catch (IOException e) {
                    // Handle the exception if image reading fails
                    System.out.println("Error reading image: " + file.getName());
                }
            }
        }

        return similarImages;
    }
}
