package search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;


//finish color
public class ImageSearcherGUI {
    private static final int THUMBNAIL_SIZE = 200;

    private JFrame frame;
    private JPanel imagePanel;
    private JLabel resultLabel;

    private File selectedImageFile;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageSearcherGUI().createAndShowGUI();
            }
        });
    }

    void createAndShowGUI() {
        frame = new JFrame("Image Searcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create the image panel
        imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create the result label
        resultLabel = new JLabel("No similar images found.");
        mainPanel.add(resultLabel, BorderLayout.SOUTH);

        // Create the select button
        JButton selectButton = new JButton("Select Image");
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                    searchAndDisplaySimilarImages();
                }
            }
        });
        mainPanel.add(selectButton, BorderLayout.NORTH);

        // Set the main panel as the content pane
        frame.getContentPane().add(mainPanel);

        // Display the window
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void searchAndDisplaySimilarImages() {
        // Clear existing images from the panel
        imagePanel.removeAll();

        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(selectedImageFile);
            int[] inputHistogram = calculateHistogram(inputImage);

            // Specify the folder to search for similar images
            String folderPath = "C:\\Users\\User 2004\\Desktop\\Color";

            // Create a list to store the similar images
            List<SimilarImage> similarImages = new ArrayList<>();

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

                    // Add the image and its similarity score to the list if similarity score is above threshold
                    double similarityThreshold = 0.5; //change between 0 and 1

                    if (intersectionScore >= similarityThreshold) {
                        similarImages.add(new SimilarImage(file, intersectionScore));
                    }
                }
            }

            boolean similarImagesFound = false;

            // Display the similar images
            for (SimilarImage similarImage : similarImages) {
                // Load the image from the file
                BufferedImage image = ImageIO.read(similarImage.file);

                // Create the thumbnail image
                Image thumbnail = image.getScaledInstance(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Image.SCALE_SMOOTH);

                // Create the image label
                JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));
                imagePanel.add(imageLabel);

                similarImagesFound = true;
            }

            if (similarImagesFound) {
                resultLabel.setText("Similar images found.");
            } else {
                resultLabel.setText("No similar images found.");
            }

            // Update the UI
            imagePanel.revalidate();
            imagePanel.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] calculateHistogram(BufferedImage image) {
        // Get the width and height of the image
        int width = image.getWidth();
        int height = image.getHeight();

        // Create an array to store the histogram bins
        int numBins = 256; // Assuming 8-bit grayscale image
        int[] histogram = new int[numBins];

        // Iterate over each pixel of the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the pixel value at the current coordinates
                int rgb = image.getRGB(x, y);
                int grayValue = (rgb >> 16) & 0xFF; // Assuming grayscale image

                // Increment the histogram bin corresponding to the pixel value
                histogram[grayValue]++;
            }
        }

        return histogram;
    }

    private double compareHistogramsIntersection(int[] histogram1, int[] histogram2) {
        // Ensure that the histograms have the same length
        if (histogram1.length != histogram2.length) {
            throw new IllegalArgumentException("Histograms must have the same length.");
        }

        int numBins = histogram1.length;
        int sumMin = 0;
        int sumMax = 0;

        for (int i = 0; i < numBins; i++) {
            // Calculate the minimum value between the two histograms at the current bin
            int minValue = Math.min(histogram1[i], histogram2[i]);
            sumMin += minValue;

            // Calculate the maximum value between the two histograms at the current bin
            int maxValue = Math.max(histogram1[i], histogram2[i]);
            sumMax += maxValue;
        }

        // Calculate the similarity score using histogram intersection formula
        double intersectionScore = (double) sumMin / sumMax;

        System.out.println(intersectionScore);
        return intersectionScore;
    }

    private class SimilarImage {
        private File file;
        private double similarityScore;

        public SimilarImage(File file, double similarityScore) {
            this.file = file;
            this.similarityScore = similarityScore;
        }
    }
}
