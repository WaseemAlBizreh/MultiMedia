package search;

import java.awt.Color;
        import java.awt.image.BufferedImage;
        import java.io.File;
        import java.io.IOException;
        import java.util.*;

        import javax.imageio.ImageIO;

public class DominantColorSearcher {
    private static final int NUM_DOMINANT_COLORS = 5; // Number of dominant colors to extract

    public static List<Color> extractDominantColors(BufferedImage image) {
        // Calculate the histogram of the image
        int[] histogram = calculateHistogram(image);

        // Sort the histogram in descending order of color count
        List<Integer> sortedColors = getSortedColors(histogram);

        // Extract the dominant colors from the sorted histogram
        List<Color> dominantColors = getDominantColors(sortedColors);

        return dominantColors;
    }

    private static int[] calculateHistogram(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] histogram = new int[256 * 256 * 256];

        // Iterate over the pixels of the image and count the occurrences of each color
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                histogram[rgb]++;
            }
        }

        return histogram;
    }

    private static List<Integer> getSortedColors(int[] histogram) {
        List<Integer> colors = new ArrayList<>();

        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > 0) {
                colors.add(i);
            }
        }

        // Sort the colors in descending order of count
        colors.sort(Comparator.comparingInt(c -> histogram[c]));
        Collections.reverse(colors);

        return colors;
    }

    private static List<Color> getDominantColors(List<Integer> sortedColors) {
        List<Color> dominantColors = new ArrayList<>();

        // Select the top N colors with the highest counts as dominant colors
        for (int i = 0; i < Math.min(NUM_DOMINANT_COLORS, sortedColors.size()); i++) {
            int rgb = sortedColors.get(i);
            Color color = new Color(rgb);
            dominantColors.add(color);
        }

        return dominantColors;
    }

    public static List<File> searchSimilarImages(File folder, List<Color> inputColors, double threshold) {
        List<File> similarImages = new ArrayList<>();

        File[] imageFiles = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") ||
                        name.toLowerCase().endsWith(".png"));

        for (File imageFile : imageFiles) {
            try {
                BufferedImage image = ImageIO.read(imageFile);

                // Extract the dominant colors of the image
                List<Color> imageDominantColors = extractDominantColors(image);

                // Calculate the similarity score between the input colors and the image colors
                double similarityScore = calculateSimilarityScore(inputColors, imageDominantColors);

                // Check if the similarity score is below the threshold
                if (similarityScore <= threshold) {
                    similarImages.add(imageFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return similarImages;
    }

    private static double calculateSimilarityScore(List<Color> colors1, List<Color> colors2) {
        // Calculate the similarity score between two sets of colors
        // Here, you can use a suitable distance metric such as Euclidean distance or cosine similarity
        // For simplicity, let's calculate the average Euclidean distance between each pair of colors

        int numColors = Math.min(colors1.size(), colors2.size());
        double sumDistance = 0.0;

        for (int i = 0; i < numColors; i++) {
            Color color1 = colors1.get(i);
            Color color2 = colors2.get(i);
            double distance = calculateColorDistance(color1, color2);
            sumDistance += distance;
        }

        return sumDistance / numColors;
    }

    private static double calculateColorDistance(Color color1, Color color2) {
        // Calculate the Euclidean distance between two colors
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
        return distance;
    }

    public static void main(String[] args) {
        BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(new File("input_image.jpg")); // Replace with the path to your input image
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Color> inputColors = extractDominantColors(inputImage);

        File folder = new File("search_folder"); // Replace with the path to your search folder
        double threshold = 50.0; // Adjust the threshold as needed

        List<File> similarImages = searchSimilarImages(folder, inputColors, threshold);

        // Display the similar images
        for (File imageFile : similarImages) {
            System.out.println(imageFile.getName());
        }
    }
}
