package main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GefenSystemDataPanel extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    /**
     * Create the frame.
     */
    public GefenSystemDataPanel() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Gefen System Data Panel");
        setBounds(100, 100, 954, 520);
        setResizable(false);

        // Initialize Content Pane with Background Image
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon(getClass().getClassLoader().getResource("Untitled design-8.png"));
                if (backgroundIcon.getImage() != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    System.err.println("Background image not found!");
                }
            }
        };

        contentPane.setLayout(null);
        setContentPane(contentPane); // Ensure it is set as the content pane

        JButton btnExportXML = new JButton("Export to XML");
        btnExportXML.setBounds(300, 178, 350, 40);
        contentPane.add(btnExportXML);
        btnExportXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main mainInstance = new Main(); // Create an instance of Main
                mainInstance.exportToXML(); // Call the method
            }
        });

        JButton btnImportXML = new JButton("Import from XML");
        btnImportXML.setBounds(300, 230, 350, 40);
        contentPane.add(btnImportXML);
        btnImportXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main mainInstance = new Main(); // Create an instance of Main
                mainInstance.importFromXML(); // Call the method
            }
        });

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(17, 17, 133, 40);
        btnLogout.addActionListener(e -> logout());
        contentPane.add(btnLogout);
    }

    /**
     * Handles logout and redirects to Login Panel.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose(); // Close the current panel
            new FirstPage().setVisible(true); // Redirect to login
        }
    }
}
