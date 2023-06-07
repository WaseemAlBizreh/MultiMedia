package k_means_clustering;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeansClustering {

    private static final int MAX_ITERATIONS = 100;

    public static List<Color> performColorQuantization(BufferedImage image, int k) {
        // Step 1: Collect pixel colors from the image
        List<Color> pixels = getPixelColors(image);

        // Step 2: Initialize centroids randomly
        List<Color> centroids = initializeCentroids(k);

        // Step 3: Run k-means clustering
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            List<List<Color>> clusters = new ArrayList<>(k);
            for (int i = 0; i < k; i++) {
                clusters.add(new ArrayList<>());
            }

            // Assign each pixel to the nearest centroid
            for (Color pixel : pixels) {
                int nearestCentroidIndex = getNearestCentroidIndex(pixel, centroids);
                clusters.get(nearestCentroidIndex).add(pixel);
            }

            // Update centroids based on the assigned pixels
            List<Color> newCentroids = new ArrayList<>();
            for (List<Color> cluster : clusters) {
                if (!cluster.isEmpty()) {
                    Color centroid = calculateCentroid(cluster);
                    newCentroids.add(centroid);
                }
            }

            // Check convergence
            if (newCentroids.equals(centroids)) {
                break;
            }

            centroids = newCentroids;
        }

        return centroids;
    }

    private static List<Color> getPixelColors(BufferedImage image) {
        List<Color> pixels = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(image.getRGB(x, y), true);
                pixels.add(pixel);
            }
        }

        return pixels;
    }

    private static List<Color> initializeCentroids(int k) {
        List<Color> centroids = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < k; i++) {
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            Color centroid = new Color(red, green, blue);
            centroids.add(centroid);
        }

        return centroids;
    }

    private static int getNearestCentroidIndex(Color pixel, List<Color> centroids) {
        int nearestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            double distance = calculateDistance(pixel, centroids.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                nearestIndex = i;
            }
        }

        return nearestIndex;
    }

    private static double calculateDistance(Color color1, Color color2) {
        int redDiff = color1.getRed() - color2.getRed();
        int greenDiff = color1.getGreen() - color2.getGreen();
        int blueDiff = color1.getBlue() - color2.getBlue();

        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private static Color calculateCentroid(List<Color> pixels) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        for (Color pixel : pixels) {
            redSum += pixel.getRed();
            greenSum += pixel.getGreen();
            blueSum += pixel.getBlue();
        }

        int count = pixels.size();
        int redAverage = redSum / count;
        int greenAverage = greenSum / count;
        int blueAverage = blueSum / count;

        return new Color(redAverage, greenAverage, blueAverage);
    }
}