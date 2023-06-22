package search;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageCropByMouse extends JFrame {
    private JLabel originalImageLabel;
    private JLabel croppedImageLabel;
    private BufferedImage originalImage;
    private Rectangle cropRectangle;

    public ImageCropByMouse() {
        super("ImageCropByMouse");

        // Create a panel for the input controls
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Add a file chooser for selecting the input image
        JFileChooser fileChooser = new JFileChooser();
        inputPanel.add(new JLabel("Select input image:"));
        JButton selectImageButton = new JButton("Select Image");
        selectImageButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    originalImage = ImageIO.read(selectedFile);
                    originalImageLabel.setIcon(new ImageIcon(originalImage));
                    croppedImageLabel.setIcon(null);
                    cropRectangle = null;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading image file: " + ex.getMessage());
                }
            }
        });
        inputPanel.add(selectImageButton);

        // Add a button for cropping the image
        JButton cropButton = new JButton("Crop Image");
        cropButton.addActionListener(e -> {
            if (cropRectangle != null) {
                BufferedImage croppedImage = originalImage.getSubimage(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height);
                croppedImageLabel.setIcon(new ImageIcon(croppedImage));
            } else {
                JOptionPane.showMessageDialog(this, "Please select a crop area.");
            }
        });
        inputPanel.add(cropButton);

        // Add the input panel to the main window
        add(inputPanel, BorderLayout.NORTH);

        // Create a panel for displaying the images
        JPanel imagePanel = new JPanel();
        originalImageLabel = new JLabel();
        croppedImageLabel = new JLabel();
        imagePanel.add(originalImageLabel);
        imagePanel.add(croppedImageLabel);
        add(imagePanel, BorderLayout.CENTER);

        // Set window properties
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Add a mouse listener to capture the crop area
        originalImageLabel.addMouseListener(new MouseAdapter() {
            private Point startPoint;

            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point endPoint = e.getPoint();
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(endPoint.x - startPoint.x);
                int height = Math.abs(endPoint.y - startPoint.y);
                cropRectangle = new Rectangle(x, y, width, height);
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (originalImage != null && cropRectangle != null) {
            g.drawImage(originalImage, 0, 0, null);
            g.setColor(Color.RED);
            g.drawRect(cropRectangle.x, cropRectangle.y, cropRectangle.width, cropRectangle.height);
        }
    }

    public static void main(String[] args) {
        new ImageCropByMouse();
    }
}
