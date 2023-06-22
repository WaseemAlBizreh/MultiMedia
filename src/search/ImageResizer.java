package search;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
public class ImageResizer {
    private File selectedFile;
    private JFrame frame;
    private JLabel imageLabel;
    private JTextField widthField;
    private JTextField heightField;

    public ImageResizer()
    {
        // Create the frame and components
        frame = new JFrame("Image Resizer");
        JPanel panel = new JPanel();
        JButton selectButton = new JButton("Select Image");
        selectButton.addActionListener(e -> selectImage());
        widthField = new JTextField(5);
        heightField = new JTextField(5);
        JButton resizeButton = new JButton("Resize Image");
        resizeButton.addActionListener(e -> resizeImage());
        imageLabel = new JLabel();

        // Add the components to the panel
        panel.add(selectButton);
        panel.add(new JLabel("Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);
        panel.add(resizeButton);

        // Add the panel and image label to the frame
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(imageLabel, BorderLayout.CENTER);

        // Configure the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



    private void selectImage()
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
            displayImage(selectedFile);
        }
    }



    private void displayImage(File imageFile)
    {
        try
        {
            BufferedImage image = ImageIO.read(imageFile);
            imageLabel.setIcon(new ImageIcon(image));
            frame.pack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private void resizeImage()
    {
        try
        {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            BufferedImage originalImage = ImageIO.read(selectedFile);
            BufferedImage resizedImage = resizeImage(originalImage, width, height);
            imageLabel.setIcon(new ImageIcon(resizedImage));
            frame.pack();
            ImageIO.write(resizedImage, "jpg", new File("C:\\Users\\User\\Pictures\\resized_image.jpg"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height)
    {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageResizer());
    }

}