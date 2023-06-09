package octree;

import ColorHistogram.ColorHistogramCalculation;
import ColorPalette.ColorPaletteDisplay;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OctreeExample extends JFrame implements ActionListener {
    JButton button;
    JLabel label;
    JLabel label2;
    JLabel label3;
    JFileChooser fileChooser;
    File selectedFile;

    public OctreeExample() {
        setTitle("Octree Clistering");
        setSize(300, 300);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        label = new JLabel("No file selected");
        button = new JButton("Select file");
        button.addActionListener(this);

        label2 = new JLabel("");
        label3 = new JLabel("");

        add(button);
        add(label);
        add(label2);
        add(label3);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            fileChooser = new JFileChooser();
            File defaultDirectory = new File("C:/Users/Dell/Desktop");
            fileChooser.setCurrentDirectory(defaultDirectory);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage inputImage = ImageIO.read(selectedFile);

                    // Define the number of colors for quantization
                    int k = 256;

                    // Perform color quantization
                    BufferedImage outputImage = OctreeQuantization.quantizeImage(inputImage, k);

                    // Save the output image
                    File outputFile = new File("images/octree-quantized-" + k + ".jpg");
                    ImageIO.write(outputImage, "jpg", outputFile);

                    System.out.println("Output image saved successfully On Your Desktop.");
                    ImageIcon icon = new ImageIcon(inputImage);
                    label.setIcon(icon);
                    label.setText("Original Image");

                    ImageIcon iconOutput = new ImageIcon(outputImage);
                    label2.setIcon(iconOutput);
                    label2.setText("Quantized Image");

                    // Indexed Image
                    BufferedImage indexedImage = new BufferedImage(outputImage.getWidth(), outputImage.getHeight(),
                            BufferedImage.TYPE_BYTE_INDEXED);
                    Graphics g = indexedImage.getGraphics();
                    g.drawImage(outputImage, 0, 0, null);
                    g.dispose();

                    ImageIcon iconIndexed = new ImageIcon(indexedImage);
                    label3.setIcon(iconIndexed);
                    label3.setText("Indexed Quantized Image");

                    ColorHistogramCalculation colorHistogramCalculation = new ColorHistogramCalculation();

                    colorHistogramCalculation.getColorHistogram("images/octree-quantized-" + k + ".jpg", 256);

                    ColorPaletteDisplay colorPaletteDisplay = new ColorPaletteDisplay();
                    colorPaletteDisplay.getColorPalette("images/octree-quantized-" + k + ".jpg");


                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            OctreeExample uploader = new OctreeExample();
            uploader.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}