package Uniform;

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

public class UniformColorQuantizationExample extends JFrame implements ActionListener {
    JButton button;
    JLabel label;
    JLabel label2;
    JLabel label3;
    JFileChooser fileChooser;
    File selectedFile;

    public UniformColorQuantizationExample() {
        setTitle("Uniform Color Quantization");
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
                    int k = 3;

                    // Perform color quantization
                    BufferedImage quantizedImage = UniformColorQuantization.quantize(inputImage, k);

                    // Create the output image with quantized colors
                    ImageIO.write(quantizedImage, "jpg", new File("C:/Users/Dell/Desktop/Uniform-Quantization-" + k + ".jpg"));

                    // Save the output image
                    File outputFile = new File("images/Uniform-quantized-" + k + ".jpg");
                    ImageIO.write(quantizedImage, "jpg", outputFile);

                    System.out.println("Output image saved successfully On Your Desktop.");
                    ImageIcon icon = new ImageIcon(inputImage);
                    label.setIcon(icon);
                    label.setText("Original Image");

                    ImageIcon iconOutput = new ImageIcon(quantizedImage);
                    label2.setIcon(iconOutput);
                    label2.setText("Quantized Image");

                    // Indexed Image
                    BufferedImage indexedImage = new BufferedImage(quantizedImage.getWidth(), quantizedImage.getHeight(),
                            BufferedImage.TYPE_BYTE_INDEXED);
                    Graphics g = indexedImage.getGraphics();
                    g.drawImage(quantizedImage, 0, 0, null);
                    g.dispose();

                    ImageIcon iconIndexed = new ImageIcon(indexedImage);
                    label3.setIcon(iconIndexed);
                    label3.setText("Indexed Quantized Image");

                    ColorHistogramCalculation colorHistogramCalculation = new ColorHistogramCalculation();

                    colorHistogramCalculation.getColorHistogram("images/Uniform-quantized-" + k + ".jpg", 256);

                    ColorPaletteDisplay colorPaletteDisplay = new ColorPaletteDisplay();
                    colorPaletteDisplay.getColorPalette("images/Uniform-quantized-" + k + ".jpg");


                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UniformColorQuantizationExample uploader = new UniformColorQuantizationExample();
            uploader.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}