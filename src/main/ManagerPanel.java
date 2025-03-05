package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ManagerPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
            	ManagerPanel frame = new ManagerPanel();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the frame.
     */
    public ManagerPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Employee Panel");
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
        gbc.insets = new Insets(10, 10, 10, 10); // Space between buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Buttons (from the diagram for Employee actions)
        String[] buttonLabels = {
            "Generate Wine Recommendations", "Manage Customers", "Place Order", "Generate Unproductive Employees Report",
            "Recommend Wine Types", "Manage Wine Types", "Manage Occasions", "Manage Food",
            "Update Wine Info", "Manage Employees", "Generate Inventory Report", "Import Producers/Wines",
            "Import Wine Quantities", "View Wine Information"
        };

        // Layout - 3 Columns (Left, Center, Right)
        JPanel leftPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JPanel rightPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        leftPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        rightPanel.setOpaque(false);

        // Distribute buttons across the three panels like the provided image
        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = createStyledButton(buttonLabels[i]);
            if (i % 3 == 0) {
                leftPanel.add(button);
            } else if (i % 3 == 1) {
                centerPanel.add(button);
            } else {
                rightPanel.add(button);
            }
        }

        // Add panels to main layout
        gbc.gridx = 0;
        contentPane.add(leftPanel, gbc);

        gbc.gridx = 1;
        contentPane.add(centerPanel, gbc);

        gbc.gridx = 2;
        contentPane.add(rightPanel, gbc);
    }

    /**
     * Creates a styled button with shorter width.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 40)); // Adjusted width & height
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