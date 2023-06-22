package search;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class ImageDateSearchGUI {
    private JFrame frame;
    private JTextField imageField;
    private JTextField folderField;
    private JTextArea resultArea;

    public void run() {
        // Create the main frame
        frame = new JFrame("Image Date Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the input fields and the search button
        JLabel imageLabel = new JLabel("Input Image:");
        imageField = new JTextField(20);
        JButton imageButton = new JButton("Browse...");
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        JLabel folderLabel = new JLabel("Search Folder:");
        folderField = new JTextField(20);
        JButton folderButton = new JButton("Browse...");
        folderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFolder();
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchSimilarImages();
            }
        });

        // Create the result area
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        // Create the main panel and add the components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.add(imageLabel);
        imagePanel.add(imageField);
        imagePanel.add(imageButton);
        mainPanel.add(imagePanel);

        JPanel folderPanel = new JPanel(new FlowLayout());
        folderPanel.add(folderLabel);
        folderPanel.add(folderField);
        folderPanel.add(folderButton);
        mainPanel.add(folderPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        mainPanel.add(buttonPanel);

        mainPanel.add(new JScrollPane(resultArea));

        // Add the main panel to the frame
        frame.add(mainPanel);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg") || f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png)";
            }
        });
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            imageField.setText(file.getAbsolutePath());
        }
    }

    private void selectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            folderField.setText(file.getAbsolutePath());
        }
    }

    private void searchSimilarImages() {
        String inputImagePath = imageField.getText();
        String folderPath = folderField.getText();
        long dateTolerance = 60 * 60 * 1000; // Date tolerance in hour (1 hour)

        try {
            // Load the input image
            BufferedImage inputImage = ImageIO.read(new File(inputImagePath));

            // Get the creation or modification date of the input image
            FileTime inputImageDate= Files.getLastModifiedTime(Paths.get(inputImagePath));

            // Search for similar images by date
            List<String> similarImagePaths = searchSimilarImagesByDate(folderPath, inputImageDate, dateTolerance);

            // Display the similar images
            resultArea.setText("Similar images by date:\n");

            JPanel imagePanel = new JPanel(new GridLayout(0, 4, 5, 5)); // 4 images per row
            for (String imagePath : similarImagePaths) {
                // Load the similar image
                BufferedImage similarImage = ImageIO.read(new File(folderPath, imagePath));

                // Scale the image to fit in the JLabel
                Image scaledImage = similarImage.getScaledInstance(150, -1, Image.SCALE_SMOOTH);

                // Create the JLabel and add it to the image panel
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imagePanel.add(imageLabel);
            }

            // Add the image panel to a scroll pane and display it
            JScrollPane scrollPane = new JScrollPane(imagePanel);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(frame, scrollPane, "Similar Images", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ImageDateSearchGUI().run();
            }
        });
    }
}