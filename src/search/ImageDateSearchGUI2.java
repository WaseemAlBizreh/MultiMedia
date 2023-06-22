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

public class ImageDateSearchGUI2 {
    private JFrame frame;
    private JTextField imageField;
    private JTextField folderField;
    private JTextArea resultArea;
    private JSpinner toleranceSpinner; // Replace JSlider with JSpinner

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

        JLabel toleranceLabel = new JLabel("Date Tolerance (minutes):"); // Change label text
        SpinnerModel toleranceModel = new SpinnerNumberModel(60, 1, 1440, 1); // Set initial value and range
        toleranceSpinner = new JSpinner(toleranceModel);
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

        JPanel tolerancePanel = new JPanel(new FlowLayout());
        tolerancePanel.add(toleranceLabel);
        tolerancePanel.add(toleranceSpinner); // Add JSpinner instead of JSlider
        mainPanel.add(tolerancePanel);

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
        // Get the input image and the search folder
        File inputImage = new File(imageField.getText());
        File searchFolder = new File(folderField.getText());

        // Get the date tolerance from the spinner
        long dateTolerance = (long) toleranceSpinner.getValue() * 60 * 1000L; // Convert minutes to milliseconds

        // Search for similar images in the search folder
        List<File> similarImages = findSimilarImages(inputImage, searchFolder, dateTolerance);

        // Display the result
        if (similarImages.isEmpty()) {
            resultArea.setText("No similar images found.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Similar images found:\n");
            for (File file : similarImages) {
                sb.append(file.getAbsolutePath()).append("\n");
            }
            resultArea.setText(sb.toString());
        }
    }

    private List<File> findSimilarImages(File inputImage, File searchFolder, long dateTolerance) {
        List<File> similarImages = new ArrayList<>();
        try {
            // Get the creation time of the input image
            BasicFileAttributes attributes = Files.readAttributes(inputImage.toPath(), BasicFileAttributes.class);
            FileTime creationTime = attributes.creationTime();

            // Search for image files in the search folder and compare their creation times with the input image's creation time
            Files.walkFileTree(searchFolder.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (Files.isRegularFile(file)) {
                        String fileName = file.toString();
                        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                        if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")) {
                            FileTime fileCreationTime = attrs.creationTime();
                            long timeDiff = Math.abs(creationTime.toMillis() - fileCreationTime.toMillis());
                            if (timeDiff <= dateTolerance) { // Compare creation times within the tolerance
                                BufferedImage inputImageBI = ImageIO.read(inputImage);
                                BufferedImage searchImageBI = ImageIO.read(file.toFile());
                                if (isSimilarImage(inputImageBI, searchImageBI)) {
                                    similarImages.add(file.toFile());
                                }
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return similarImages;
    }

    private boolean isSimilarImage(BufferedImage image1, BufferedImage image2) {
        // Compare the images pixel by pixel
        for (int x = 0; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ImageDateSearchGUI gui = new ImageDateSearchGUI();
        gui.run();
    }
}