package search;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class KMeans {
    private static int k; // Number of clusters
    private static List<Color> centroids; // Centroids of the clusters

    public KMeans(int k) {
        this.k = k;
        this.centroids = new ArrayList<>();
    }

    public static List<Color> cluster(List<Color> data) {
        if (data.size() <= k) {
            return data; // No need to cluster if the number of data points is less than or equal to k
        }

        // Initialize centroids randomly
        initializeCentroids(data);

        // Perform k-means clustering
        boolean centroidsChanged;
        do {
            // Assign data points to the nearest centroid
            List<List<Color>> clusters = assignDataPoints(data);

            // Update centroids
            centroidsChanged = updateCentroids(clusters);
        } while (centroidsChanged);

        return centroids;
    }

    private static void initializeCentroids(List<Color> data) {
        // Shuffle the data
        List<Color> shuffledData = new ArrayList<>(data);
        java.util.Collections.shuffle(shuffledData);

        // Take the first k colors as initial centroids
        centroids.clear();
        centroids.addAll(shuffledData.subList(0, k));
    }

    private static List<List<Color>> assignDataPoints(List<Color> data) {
        List<List<Color>> clusters = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }

        // Assign each data point to the nearest centroid
        for (Color point : data) {
            int nearestCentroidIndex = findNearestCentroid(point);
            clusters.get(nearestCentroidIndex).add(point);
        }

        return clusters;
    }

    private static int findNearestCentroid(Color point) {
        int nearestCentroidIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < centroids.size(); i++) {
            Color centroid = centroids.get(i);
            double distance = calculateColorDistance(point, centroid);

            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroidIndex = i;
            }
        }

        return nearestCentroidIndex;
    }

    private static double calculateColorDistance(Color color1, Color color2) {
        int redDiff = color1.getRed() - color2.getRed();
        int greenDiff = color1.getGreen() - color2.getGreen();
        int blueDiff = color1.getBlue() - color2.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    private static boolean updateCentroids(List<List<Color>> clusters) {
        boolean centroidsChanged = false;

        for (int i = 0; i < k; i++) {
            List<Color> cluster = clusters.get(i);

            if (!cluster.isEmpty()) {
                Color newCentroid = calculateClusterCentroid(cluster);

                if (!centroids.get(i).equals(newCentroid)) {
                    centroids.set(i, newCentroid);
                    centroidsChanged = true;
                }
            }
        }

        return centroidsChanged;
    }

    private static Color calculateClusterCentroid(List<Color> cluster) {
        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        for (Color point : cluster) {
            redSum += point.getRed();
            greenSum += point.getGreen();
            blueSum += point.getBlue();
        }

        int clusterSize = cluster.size();
        int redAverage = redSum / clusterSize;
        int greenAverage = greenSum / clusterSize;
        int blueAverage = blueSum / clusterSize;

        return new Color(redAverage, greenAverage, blueAverage);
    }
}
