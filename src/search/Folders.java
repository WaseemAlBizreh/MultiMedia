package search;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Folders extends JFrame implements ActionListener {

    private BufferedImage queryImage;
    private File[] foldersToSearch;

    private JLabel queryImageLabel;
    private JPanel similarImagesPanel;

    private static final double COLOR_SIMILARITY_THRESHOLD = 100.0;

    public Folders() {
        setTitle("Image Search");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JPanel queryPanel = createQueryPanel();
        add(queryPanel);

        JPanel similarImagesPanel = createSimilarImagesPanel();
        JScrollPane scrollPane = new JScrollPane(similarImagesPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        setVisible(true);
    }

    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(this);
        panel.add(selectImageButton);

        JButton selectFoldersButton = new JButton("Select Folders");
        selectFoldersButton.addActionListener(this);
        panel.add(selectFoldersButton);

        queryImageLabel = new JLabel();
        queryImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(queryImageLabel);

        return panel;
    }

    private JPanel createSimilarImagesPanel() {
        similarImagesPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        return similarImagesPanel;
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                queryImage = ImageIO.read(selectedFile);
                displayImage(queryImage, queryImageLabel);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectFolders() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Folders to Search");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            foldersToSearch = fileChooser.getSelectedFiles();
        }
    }

    private void searchForSimilarImages() {
        if (queryImage == null || foldersToSearch == null) {
            JOptionPane.showMessageDialog(this, "Please select an image and folders first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        similarImagesPanel.removeAll();

        for (File folder : foldersToSearch) {
            File[] imageFiles = folder.listFiles();
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    try {
                        BufferedImage image = ImageIO.read(imageFile);
                        if (isColorSimilar(queryImage, image)) {
                            JLabel similarImageLabel = new JLabel();
                            similarImageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            displayImage(image, similarImageLabel);
                            similarImagesPanel.add(similarImageLabel);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        similarImagesPanel.revalidate();
        similarImagesPanel.repaint();
    }

    private boolean isColorSimilar(BufferedImage image1, BufferedImage image2) {
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return false;
        }

        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                Color color1 = new Color(image1.getRGB(x, y));
                Color color2 = new Color(image2.getRGB(x, y));

                double colorDifference = Math.sqrt(
                        Math.pow(color2.getRed() - color1.getRed(), 2) +
                                Math.pow(color2.getGreen() - color1.getGreen(), 2) +
                                Math.pow(color2.getBlue() - color1.getBlue(), 2)
                );

                if (colorDifference > COLOR_SIMILARITY_THRESHOLD) {
                    return false;
                }
            }
        }

        return true;
    }

    private void displayImage(BufferedImage image, JLabel label) {
        Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Select Image")) {
            selectImage();
        } else if (command.equals("Select Folders")) {
            selectFolders();
        }
        searchForSimilarImages();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Folders();
            }
        });
    }
}
