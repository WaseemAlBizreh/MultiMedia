package search_size_date;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageSearch {

    public static void main(String[] args) {

        // Set the directory where the images are located
        File directory = new File("path/to/images");

        // Set the image creation date range to search for (format: yyyy-MM-dd)
        String startDateString = "2021-01-01";
        String endDateString = "2021-12-31";

        // Set the minimum and maximum image size (in bytes) to search for
        long minSize = 10000;
        long maxSize = 1000000;

        // Get the list of image files in the directory
        File[] files = directory.listFiles();

        // Create a list to store the matching images
        List<File> matchingImages = new ArrayList<>();

        // Create a date formatter object to parse the date strings
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        // Loop through the image files
        for (File file : files) {

            // Check if the file is an image (you can add more file extensions if needed)
            if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {

                // Get the file creation date
                Date creationDate = new Date(file.lastModified());

                // Check if the creation date is within the specified range
                try {
                    Date startDate = dateFormatter.parse(startDateString);
                    Date endDate = dateFormatter.parse(endDateString);

                    if (creationDate.after(startDate) && creationDate.before(endDate)) {

                        // Check if the file size is within the specified range
                        if (file.length() >= minSize && file.length() <= maxSize) {

                            // Add the matching image to the list
                            matchingImages.add(file);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // Display the matching images
        for (File image : matchingImages) {
            System.out.println(image.getName());
        }
    }
}