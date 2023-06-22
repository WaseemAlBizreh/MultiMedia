package crop;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ImageCrop extends JFrame {
    private JLabel originalImageLabel;
    private JLabel croppedImageLabel;
    private JTextField xField;
    private JTextField yField;

    public ImageCrop() {
        super("ImageProcessorUI");

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
                    BufferedImage originalImage = ImageIO.read(selectedFile);
                    originalImageLabel.setIcon(new ImageIcon(originalImage));
                    croppedImageLabel.setIcon(null);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading image file: " + ex.getMessage());
                }
            }
        });
        inputPanel.add(selectImageButton);

        // Add fields for selecting the crop area
        inputPanel.add(new JLabel("Select crop area:"));
        xField = new JTextField(10);
        yField = new JTextField(10);
        inputPanel.add(new JLabel("x:"));
        inputPanel.add(xField);
        inputPanel.add(new JLabel("y:"));
        inputPanel.add(yField);

        // Add a button for cropping the image
        JButton cropButton = new JButton("Crop Image");

        cropButton.addActionListener(e -> {
            ImageIcon icon = (ImageIcon) originalImageLabel.getIcon();
            Image originalImage = icon.getImage();
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            int width = 100; // width of the crop area
            int height = 100; // height of the crop area

            // Validate crop area coordinates
            if (x < 0 || y < 0 || x + width > originalImage.getWidth(null) || y + height > originalImage.getHeight(null)) {
                JOptionPane.showMessageDialog(this, "Invalid crop area!");
                return;
            }

            BufferedImage bufferedImage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics g = bufferedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, null);
            g.dispose();
            BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
            croppedImageLabel.setIcon(new ImageIcon(croppedImage));
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
    }

    public static void main(String[] args) {
        new ImageCrop();
    }
}
