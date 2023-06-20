package search;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ImageColorSearchGUI {
    private JFrame frame;
    private JPanel imagePanel;
    private List<Color> dominantColors;

    public ImageColorSearchGUI() {
        frame = new JFrame("Image Color Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        imagePanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane);

        dominantColors = new ArrayList<>();

        createColorSearchButton();
        createCropImageButton();
        createResizeImageButton();

        frame.setVisible(true);
    }

    private void createColorSearchButton() {
        JButton colorSearchButton = new JButton("Select Colors and Search");
        colorSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectColorsAndSearch();
            }
        });

        frame.add(colorSearchButton);
    }

    private void createCropImageButton() {
        JButton cropImageButton = new JButton("Crop Image");
        cropImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cropImage();
            }
        });

        frame.add(cropImageButton);
    }

    private void createResizeImageButton() {
        JButton resizeImageButton = new JButton("Resize Image");
        resizeImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizeImage();
            }
        });

        frame.add(resizeImageButton);
    }

    private void selectColorsAndSearch() {
        // Clear the previous search results
        imagePanel.removeAll();
        imagePanel.revalidate();
        imagePanel.repaint();

        // Choose two colors using JColorChooser
        Color color1 = JColorChooser.showDialog(frame, "Select Color 1", null);
        Color color2 = JColorChooser.showDialog(frame, "Select Color 2", null);

        dominantColors.clear();
        dominantColors.add(color1);
        dominantColors.add(color2);

        // Get the folder paths to search for similar color images
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setDialogTitle("Select Folders");
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setMultiSelectionEnabled(true);

        int returnVal = folderChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] folders = folderChooser.getSelectedFiles();

            for (File folder : folders) {
                File[] files = folder.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            try {
                                BufferedImage image = ImageIO.read(file);

                                // Check if the image contains similar colors
                                if (containsSimilarColors(image, dominantColors)) {
                                    // Create the thumbnail image
                                    Image thumbnail = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

                                    // Create the image label
                                    JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));
                                    imagePanel.add(imageLabel);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            // Update the UI
            imagePanel.revalidate();
            imagePanel.repaint();
        }
    }

    private boolean containsSimilarColors(BufferedImage image, List<Color> colors) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                Color pixelColor = new Color(rgb);

                // Check if the pixel color is similar to any of the dominant colors
                for (Color color : colors) {
                    if (isSimilarColor(pixelColor, color)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isSimilarColor(Color color1, Color color2) {
        int redDiff = Math.abs(color1.getRed() - color2.getRed());
        int greenDiff = Math.abs(color1.getGreen() - color2.getGreen());
        int blueDiff = Math.abs(color1.getBlue() - color2.getBlue());

        // Adjust the color difference threshold as needed
        int threshold = 50;

        return redDiff <= threshold && greenDiff <= threshold && blueDiff <= threshold;
    }

    private void cropImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image to Crop");

        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                BufferedImage image = ImageIO.read(file);

                // Create a new frame for the cropped image
                JFrame cropFrame = new JFrame("Cropped Image");
                cropFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cropFrame.setSize(400, 400);

                // Create the cropped image label
                JLabel cropLabel = new JLabel(new ImageIcon(image));
                cropFrame.add(cropLabel);

                // Set the crop frame visible
                cropFrame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resizeImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image to Resize");

        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                BufferedImage image = ImageIO.read(file);

                // Create a new frame for the resized image
                JFrame resizeFrame = new JFrame("Resized Image");
                resizeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                resizeFrame.setSize(400, 400);

                // Create the resized image label
                JLabel resizeLabel = new JLabel(new ImageIcon(image));
                resizeLabel.setPreferredSize(new Dimension(300, 300));
                resizeFrame.add(resizeLabel);

                // Set the resize frame visible
                resizeFrame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ImageColorSearchGUI();
    }
}
