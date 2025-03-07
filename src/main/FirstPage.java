package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FirstPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FirstPage frame = new FirstPage();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FirstPage() {
	    setTitle("Cheers Management System");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(100, 100, 954, 520);
	    setResizable(false);

	    // Main Panel with Background Image
	    contentPane = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            
	            // Load and draw the background image
                ImageIcon backgroundIcon = new ImageIcon(getClass().getClassLoader().getResource("Untitled design-7.png"));
	            if (backgroundIcon.getImage() != null) {
	                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
	            } else {
	                System.err.println("Background image not found!");
	            }
	        }
	    };

	    contentPane.setLayout(null);
	    setContentPane(contentPane);
	    
	    ImageIcon photoIcon = new ImageIcon(getClass().getClassLoader().getResource("FPage_resized.png"));
	    JLabel photoLabel = new JLabel(photoIcon);
	    // Set the position and size as needed (x, y, width, height)
	    photoLabel.setBounds(157, 156, 770, 210);
	    contentPane.add(photoLabel);
	    
	    JButton btnNewButton = new JButton("Employees");
	    btnNewButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            LoginPanel loginPanel = new LoginPanel(); // Create an instance
	            loginPanel.setVisible(true);  // Show the login panel
	        }
	    });
	    btnNewButton.setBounds(350, 325, 117, 29);
	    contentPane.add(btnNewButton);

	    
	    JButton btnNewButton_1 = new JButton("Gefen System");
	    btnNewButton_1.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            // Ask for an access code
	            String userCode = JOptionPane.showInputDialog(null, "Enter Access Code:", "Authentication", JOptionPane.PLAIN_MESSAGE);

	            // Check if the code is correct
	            if (userCode != null && userCode.equals("1234")) { // Replace "1234" with your actual access code
	                GefenSystemDataPanel gefenSystemDataPanel = new GefenSystemDataPanel(); // Create an instance
	                gefenSystemDataPanel.setVisible(true); // Open the panel
	            } else {
	                JOptionPane.showMessageDialog(null, "Incorrect code! Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });
	    btnNewButton_1.setBounds(486, 325, 117, 29);
	    contentPane.add(btnNewButton_1);

	    
	    JButton btnNewButton_2 = new JButton("Customer Order Details");
	    btnNewButton_2.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		CustomerOrderDetails CustomerOrderDetails = new CustomerOrderDetails(); // Create an instance
	    		CustomerOrderDetails.setVisible(true);
	    	}
	    });
	    btnNewButton_2.setBounds(603, 325, 192, 29);
	    contentPane.add(btnNewButton_2);


	}
}
