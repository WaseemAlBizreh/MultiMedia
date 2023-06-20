package search;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//just display color picker
public class ImageSearchColor {
    private static final int THUMBNAIL_SIZE = 200;

    private JFrame frame;
    private JPanel imagePanel;
    private JLabel resultLabel;

    private Color selectedColor;
    private double colorThreshold = 0.3; // Adjust the threshold value as needed

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageSearchColor().createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
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
                        selectedColor = null;
                        openColorPicker(image);
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

    private void openColorPicker(BufferedImage image) {
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                selectedColor = colorChooser.getColor();
                searchAndDisplaySimilarImages(image);
            }
        });

        JDialog dialog = JColorChooser.createDialog(frame, "Pick Color", true, colorChooser, null, null);
        dialog.setVisible(true);
    }

    private void searchAndDisplaySimilarImages(BufferedImage inputImage) {
        // Clear existing images from the panel
        imagePanel.removeAll();

        try {
            // Specify the folder to search for images
            String folderPath = "C:\\Users\\User 2004\\Desktop\\Color";

            // Iterate over the images in the folder
            File folder = new File(folderPath);
            File[] imageFiles = folder.listFiles();

            boolean similarImagesFound = false;

            for (File file : imageFiles) {
                if (file.isFile()) {
                    // Load the image from the file
                    BufferedImage image = ImageIO.read(file);

                    // Calculate the color similarity
                    double similarityScore = compareColor(selectedColor, image);

                    // Add the image if similarity score is above threshold
                    if (similarityScore >= colorThreshold) {
                        // Create the thumbnail image
                        Image thumbnail = image.getScaledInstance(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Image.SCALE_SMOOTH);

                        // Create the image label
                        JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));
                        imagePanel.add(imageLabel);

                        similarImagesFound = true;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double compareColor(Color selectedColor, BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        double totalSimilarity = 0.0;

        // Iterate over each pixel of the image
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the pixel color at the current coordinates
                Color pixelColor = new Color(image.getRGB(x, y));

                // Calculate the color difference using Euclidean distance in RGB color space
                double deltaRed = pixelColor.getRed() - selectedColor.getRed();
                double deltaGreen = pixelColor.getGreen() - selectedColor.getGreen();
                double deltaBlue = pixelColor.getBlue() - selectedColor.getBlue();
                double distance = Math.sqrt(deltaRed * deltaRed + deltaGreen * deltaGreen + deltaBlue * deltaBlue);

                // Calculate the similarity score for the current pixel and selected color
                double similarity = 1.0 - (distance / Math.sqrt(3.0));

                // Accumulate the similarity scores
                totalSimilarity += similarity;
            }
        }

        // Calculate the average similarity score
        double averageSimilarity = totalSimilarity / (width * height);

        return averageSimilarity;
    }
}
