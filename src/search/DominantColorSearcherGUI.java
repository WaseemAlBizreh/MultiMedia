package search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;


//choose file
public class DominantColorSearcherGUI {
    private static final int THUMBNAIL_SIZE = 200;

    private JFrame frame;
    private JPanel imagePanel;
    private JLabel resultLabel;

    private List<Color> dominantColors;
    private int k = 2; // Number of dominant colors to extract
    private double colorThreshold = 0.2; // Adjust the threshold value as needed

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DominantColorSearcherGUI().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        frame = new JFrame("Dominant Color Searcher");
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

        // Create the select image button
        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png");
                fileChooser.setFileFilter(filter);

                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        dominantColors = extractDominantColors(image);
                        searchAndDisplaySimilarImages();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        mainPanel.add(selectImageButton, BorderLayout.NORTH);

        // Set the main panel as the content pane
        frame.getContentPane().add(mainPanel);

        // Display the window
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private List<Color> extractDominantColors(BufferedImage image) {
        // Resize the image for faster processing
        int scaledWidth = 100;
        int scaledHeight = (int) ((double) image.getHeight() / image.getWidth() * scaledWidth);
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

        // Convert the scaled image to a BufferedImage
        BufferedImage scaledBufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledBufferedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        // Perform k-means clustering to extract dominant colors
        KMeans kMeans = new KMeans(k);
        List<Color> pixelColors = getPixelColors(scaledBufferedImage);
        List<Color> dominantColors = KMeans.cluster(pixelColors);

        return dominantColors;
    }

    private void searchAndDisplaySimilarImages() {
        // Clear the previous search results
        imagePanel.removeAll();
        imagePanel.revalidate();
        imagePanel.repaint();

        // Get the folder path to search for similar images
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select Folder");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = folderChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File folder = folderChooser.getSelectedFile();
            File[] files = folder.listFiles();

            if (files != null) {
                boolean similarImagesFound = false;

                for (File file : files) {
                    if (file.isFile()) {
                        try {
                            BufferedImage image = ImageIO.read(file);

                            // Extract dominant colors from the image
                            List<Color> imageDominantColors = extractDominantColors(image);

                            // Calculate the color similarity
                            double similarityScore = compareColors(dominantColors, imageDominantColors);

                            // Add the image if similarity score is above threshold
                            if (similarityScore >= colorThreshold) {
                                // Create the thumbnail image
                                Image thumbnail = image.getScaledInstance(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Image.SCALE_SMOOTH);

                                // Create the image label
                                JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));
                                imagePanel.add(imageLabel);

                                similarImagesFound = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (similarImagesFound) {
                    resultLabel.setText("Similar images found.");
                } else {
                    resultLabel.setText("No similar images found.");
                }

                // Update the UI
                imagePanel.revalidate();
                imagePanel.repaint();
            }
        }
    }

    private double compareColors(List<Color> colors1, List<Color> colors2) {
        // Calculate the average color difference between two sets of colors
        int totalDifference = 0;
        for (int i = 0; i < colors1.size(); i++) {
            Color color1 = colors1.get(i);
            Color color2 = colors2.get(i);
            int redDiff = color1.getRed() - color2.getRed();
            int greenDiff = color1.getGreen() - color2.getGreen();
            int blueDiff = color1.getBlue() - color2.getBlue();
            int difference = redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
            totalDifference += difference;
        }

        double averageDifference = (double) totalDifference / colors1.size();
        double similarityScore = 1.0 - (averageDifference / (255 * 255 * 3));
        return similarityScore;
    }

    private List<Color> getPixelColors(BufferedImage image) {
        List<Color> colors = new ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);
                colors.add(color);
            }
        }

        return colors;
    }
}
