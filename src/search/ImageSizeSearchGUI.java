package search;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageSizeSearchGUI {
    private JFrame frame;
    private JPanel inputPanel;
    private JPanel resultPanel;
    private JLabel inputLabel;
    private JLabel resultLabel;
    private JButton inputButton;
    private JButton searchButton;
    private JTextArea resultTextArea;
    private JScrollPane resultScrollPane;

    private File selectedImageFile;
    private File selectedFolder;

    public static void main(String[] args) {
        new ImageSizeSearchGUI().run();
    }

    public void run() {
        // Create the frame
        frame = new JFrame("Image Size Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Create the input panel
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputLabel = new JLabel("Input Image:");
        inputLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        inputPanel.add(inputLabel);

        inputButton = new JButton("Select Image");
        inputButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        inputButton.addActionListener(e -> selectImage());
        inputPanel.add(inputButton);

        resultLabel = new JLabel("Results:");
        resultLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        inputPanel.add(resultLabel);

        searchButton = new JButton("Search");
        searchButton.setAlignmentX(JButton.LEFT_ALIGNMENT);
        searchButton.addActionListener(e -> searchSimilarImages());
        inputPanel.add(searchButton);

        // Create the result panel
        resultPanel = new JPanel(new BorderLayout());
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultScrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        // Add the panels to the frame
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultPanel, BorderLayout.CENTER);

        // Show the frame
        frame.setVisible(true);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Input Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            inputButton.setText(selectedImageFile.getName());
        }
    }

    private void searchSimilarImages() {
        if (selectedImageFile == null) {
            resultTextArea.setText("Please select an input image.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Folder Path");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFolder = fileChooser.getSelectedFile();
        } else {
            return;
        }

        // Load the input image
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(selectedImageFile);
        } catch (IOException e) {
            resultTextArea.setText("Error occurred: " + e.getMessage());
            return;
        }

        // Get the size of the input image
        Dimension inputSize = new Dimension(inputImage.getWidth(), inputImage.getHeight());

        // Search for similar images in size
        List<File> similarImageFiles = searchSimilarImagesBySize(selectedFolder.getPath(), inputSize);

        // Display the similar images
        if (similarImageFiles.isEmpty()) {
            resultTextArea.setText("No similar images found.");
        } else {
            resultPanel.removeAll();
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
            for (File file : similarImageFiles) {
                try {
                    // Load the image
                    BufferedImage image = ImageIO.read(file);

                    // Create the thumbnail image
                    Image thumbnail = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

                    // Create the image label
                    JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));

                    // Add the image label to the result panel
                    resultPanel.add(imageLabel);
                } catch (IOException e) {
                    // Handle the exception if image reading fails
                    System.out.println("Error reading image: " + file.getName());
                }
            }
            resultPanel.revalidate();
            resultPanel.repaint();
        }
    }
    public static List<File> searchSimilarImagesBySize(String folderPath, Dimension targetSize) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<File> similarImages = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    // Load the image
                    BufferedImage image = ImageIO.read(file);

                    // Get thesize of the image
                    Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());

                    // Compare the sizes
                    if (imageSize.width == targetSize.width && imageSize.height == targetSize.height) {
                        similarImages.add(file);
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