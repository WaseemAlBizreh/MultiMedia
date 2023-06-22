package search;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

public class Colors extends JFrame {
    JButton button;
    private JFrame frame;
    private JPanel imagePanel;
    File[] selectedFolders;
    private Color selectedColor;

    public Color getSelectedColor() {
        return selectedColor;
    }

    void createAndShowGUI() {
        frame = new JFrame("Color Searcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Create the image panel
        imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create the select button
        JButton selectButton = new JButton("Select Color");
        selectButton.setPreferredSize(new Dimension(200, 50));
        selectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChooser = new JColorChooser();
                colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        selectedColor = colorChooser.getColor();
                    }
                });

                JDialog dialog = JColorChooser.createDialog(frame, "Pick Color", true, colorChooser, null, null);
                dialog.setVisible(true);
            }
        });

        JButton folderSelection = new JButton("Select Folders");
        folderSelection.setPreferredSize(new Dimension(200, 50));
        folderSelection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setMultiSelectionEnabled(true);
                File defaultDir = new File("C:/Users/DELL/IdeaProjects/MultiMedia");
                fileChooser.setCurrentDirectory(defaultDir);
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    selectedFolders = fileChooser.getSelectedFiles();
                    search(selectedFolders, selectedColor);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(selectButton);
        buttonPanel.add(folderSelection);

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Set the main panel as the content pane
        frame.getContentPane().add(mainPanel);

        // Display the window
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void search(File[] files, Color color) {
        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            File[] listFiles = file.listFiles();
                for (File file1 : listFiles) {
                    if (file1.isFile()) {
                        try {
                            BufferedImage image = ImageIO.read(file1);
                            if (hasColorPixels(image, color)) {
                                System.out.println(file1.getName() + " contains the color " + color);
                                JLabel imageLabel = new JLabel(new ImageIcon(image));
                                imagePanel.add(imageLabel);
                            }
                        } catch (IOException e) {
                            System.out.println("Error reading file: " + file1.getName());
                        }
                }
            }
        }
    }

    Colors() {
        createAndShowGUI();
    }

    public static void main(String[] args) {
        // File folder = new File("C:/Users/DELL/IdeaProjects/MultiMedia/images");
        Colors colors = new Colors();
    }

    private static boolean hasColorPixels(BufferedImage image, Color colorToSearch) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = new Color(image.getRGB(x, y));
                if (pixelColor.equals(colorToSearch)) {
                    return true;
                }
            }
        }
        return false;
    }
}