package main;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

	}

}
