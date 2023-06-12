package colorPicker;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorChooserDemo {

    public static void main(String[] args) {
        // Create a JFrame
        JFrame frame = new JFrame("Color Chooser Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Create a JButton
        JButton button = new JButton("Choose Color");

        // Add an ActionListener to the button
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Show the color chooser dialog
                Color selectedColor = JColorChooser.showDialog(frame, "Choose a Color", Color.WHITE);

                // Do something with the selected color
                if (selectedColor != null) {
                    // You can use the selectedColor here
                    System.out.println("Selected Color: " + selectedColor);
                }
            }
        });

        // Add the button to the JFrame
        frame.add(button);

        // Show the JFrame
        frame.setVisible(true);
    }
}
