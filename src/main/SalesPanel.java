package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SalesPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SalesPanel frame = new SalesPanel();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public SalesPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sales Panel");
        setBounds(100, 100, 954, 520);
        setResizable(false);

        // Main Panel with Background Image
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Load the background image
                ImageIcon backgroundIcon = new ImageIcon(getClass().getClassLoader().getResource("Untitled design-8.png"));
                
                if (backgroundIcon.getImage() != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("Background image not found!");
                }
            }
        };

        contentPane.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        setContentPane(contentPane);

        // GridBagLayout Constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Space between buttons
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;  // Disable full width

        // Buttons (from the image)
        String[] buttonLabels = {
            "Generate Wine Recommendations",
            "Manage Customers",
            "Place Order",
            "Generate Unproductive Employees Report"
        };

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createStyledButton(buttonLabels[i]);
            gbc.gridy = i; // Place each button in a new row
            contentPane.add(button, gbc);
        }
    }

    /**
     * Creates a styled button with shorter width.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(350, 40)); // Adjusted width & height
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 255, 255, 220)); // Slight transparency
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, text + " clicked!", "Action", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return button;
    }
}
