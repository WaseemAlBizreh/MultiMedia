package search;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ImageDateSearch {
    public static void main(String[] args) {
        String inputImagePath = "C:/Users/USER/Pictures/background/fence_park_trees_road_nature_autumn_foliage_1920x1280.jpg";
        String folderPath = "C:/Users/USER/Pictures/background/";
//        long dateTolerance = 4 * 60 * 1000; // Date tolerance in min (4 min)
        long dateTolerance = 60 * 60 * 1000; // Date tolerance in hour (1 hours)
//        long dateTolerance = 23 * 60 * 60 * 1000; // Date tolerance in milliseconds (23 hours)

        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

            // Get the creation or modification date of the input image
            FileTime inputImageDate = Files.getLastModifiedTime(Paths.get(inputImagePath));

            // Search for similar images by date
            List<String> similarImages = searchSimilarImagesByDate(folderPath, inputImageDate, dateTolerance);

            // Display the similar images
            System.out.println("Similar images by date:");
            for (String image : similarImages) {
                System.out.println(image);
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    public static List<String> searchSimilarImagesByDate(String folderPath, FileTime targetDate, long dateTolerance) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<String> similarImages = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    // Get the creation or modification date of the image file
                    BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    FileTime imageDate = attributes.lastModifiedTime();

                    // Compare the dates within the tolerance
                    long dateDifference = Math.abs(imageDate.toMillis() - targetDate.toMillis());
                    if (dateDifference <= dateTolerance) {
                        similarImages.add(file.getName());
                    }
                } catch (IOException e) {
                    // Handle the exception if file attributes reading fails
                    System.out.println("Error reading file attributes: " + file.getName());
                }
            }
        }

        return similarImages;
    }
}
