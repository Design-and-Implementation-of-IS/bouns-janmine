package main;



import control.DatabaseConnectionManager;
import control.WineDAO;
import control.WineImportController;
import control.WineProducerDAO;
import control.WineRecommendationService;
import model.FoodPairing;
import model.OccasionRecommendation;
import model.SweetnessLevel;
import model.Wine;
import model.WineProducer;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JFrame {

    private JPanel contentPane;
    private CardLayout cardLayout;  // CardLayout to switch between panels
    private JPanel producerReportPanel;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main frame = new Main();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);  // Set CardLayout
        setContentPane(contentPane);



        // Add different panels to the CardLayout
        contentPane.add(createMainMenuPanel(), "MainMenu");
        contentPane.add(createProducerManagementPanel(), "ManageProducers");
        contentPane.add(createViewWinesPanel(), "ViewWines");

        // Show the main menu initially
        cardLayout.show(contentPane, "MainMenu");
    }



    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout()); //
        panel.setBackground(new Color(126, 127, 127));
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getClassLoader().getResource("wine_pic4.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(400, 200, Image.SCALE_SMOOTH); // שינוי גודל התמונה
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(scaledIcon);
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.add(imageLabel);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
        }

        panel.add(imagePanel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Wine Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        buttonsPanel.add(lblTitle);

        JButton btnViewWines = new JButton("View All Wines");
        btnViewWines.addActionListener(e -> {
            JPanel viewWinesPanel = createViewWinesPanel();  // Create panel dynamically
            contentPane.add(viewWinesPanel, "ViewWines");    // Add panel to CardLayout
            cardLayout.show(contentPane, "ViewWines");       // Show the panel
        });
        buttonsPanel.add(btnViewWines);

        JButton btnManageProducers = new JButton("Manage Producers");
        btnManageProducers.addActionListener(e -> cardLayout.show(contentPane, "ManageProducers"));
        buttonsPanel.add(btnManageProducers);

        JButton btnExportXML = new JButton("Export to XML");
        btnExportXML.addActionListener(e -> exportToXML());
        buttonsPanel.add(btnExportXML);

        JButton btnImportXML = new JButton("Import from XML");
        btnImportXML.addActionListener(e -> importFromXML());
        buttonsPanel.add(btnImportXML);

        JButton btnProducerReport = new JButton("View Producer Report");
        btnProducerReport.addActionListener(e -> openProducerReportDialog());
        buttonsPanel.add(btnProducerReport);

        JButton btnGenerateReport = new JButton("Generate Report");
        btnGenerateReport.addActionListener(e -> openReportParameterDialog());
        buttonsPanel.add(btnGenerateReport);

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        buttonsPanel.add(btnExit);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }




    /**
     * Creates the panel to view all wines.
     */
    private JPanel createViewWinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("All Wines", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Table for displaying wines
        String[] columnNames = {"Wine ID", "Name", "Year", "Price", "Sweetness Level", "Product Image", "Description", "Catalog Number"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable wineTable = new JTable(tableModel);
        panel.add(new JScrollPane(wineTable), BorderLayout.CENTER);

        // Populate the table with wines
        populateWineTable(tableModel);

        // Back button to return to the main menu
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(contentPane, "MainMenu"));
        panel.add(btnBack, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Opens the producer report panel for a selected producer.
     */
    private void openProducerReportDialog() {
        List<WineProducer> producers = new WineProducerDAO().getAllWineProducers();
        String[] producerNames = producers.stream()
                .map(WineProducer::getFullName)
                .toArray(String[]::new);

        JComboBox<String> producerDropdown = new JComboBox<>(producerNames);

        int result = JOptionPane.showConfirmDialog(this, producerDropdown,
                "Select a Producer", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String selectedProducerName = (String) producerDropdown.getSelectedItem();
            WineProducer selectedProducer = producers.stream()
                    .filter(p -> p.getFullName().equals(selectedProducerName))
                    .findFirst()
                    .orElse(null);

            if (selectedProducer != null) {
                showProducerReportPanel(selectedProducer);
            }
        }
    }

    /**
     * Shows the producer report panel with wine management options.
     */
    private void showProducerReportPanel(WineProducer producer) {
        producerReportPanel = new JPanel(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Producer Report: " + producer.getFullName(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        producerReportPanel.add(lblTitle, BorderLayout.NORTH);

        // Table for displaying wines
        String[] columnNames = {"Wine ID", "Name", "Year", "Price", "Sweetness Level", "Product Image", "Description", "Catalog Number"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable wineTable = new JTable(tableModel);

        // Populate the table with wines
        for (Wine wine : producer.getWines()) {
            tableModel.addRow(new Object[]{wine.getWineId(), wine.getName(), wine.getYear(),
                    wine.getPrice(), wine.getSweetnessLevel(), wine.getProductImage(), wine.getDescription(), wine.getCatalogNumber()});
        }
        producerReportPanel.add(new JScrollPane(wineTable), BorderLayout.CENTER);

        // Buttons for add/update/delete
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton btnAddWine = new JButton("Add Wine");
        JButton btnUpdateWine = new JButton("Update Selected Wine");
        JButton btnDeleteWine = new JButton("Delete Selected Wine");

        btnAddWine.addActionListener(e -> addWine(producer, tableModel));
        btnUpdateWine.addActionListener(e -> updateWine(producer, tableModel, wineTable.getSelectedRow()));
        btnDeleteWine.addActionListener(e -> deleteWine(producer, tableModel, wineTable.getSelectedRow()));

        buttonPanel.add(btnAddWine);
        buttonPanel.add(btnUpdateWine);
        buttonPanel.add(btnDeleteWine);

        // Panel to hold both buttonPanel and Back button
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);

        // Back button to return to the main menu
        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(contentPane, "MainMenu"));
        southPanel.add(btnBack, BorderLayout.SOUTH);

        producerReportPanel.add(southPanel, BorderLayout.SOUTH);

        contentPane.add(producerReportPanel, "ProducerReport");
        cardLayout.show(contentPane, "ProducerReport");
    }
    /**
     * Populates the wine table with data from the database.
     */
    private void populateWineTable(DefaultTableModel tableModel) {
        try {
            List<Wine> wines = new WineDAO().getAllWines();
            for (Wine wine : wines) {
                tableModel.addRow(new Object[]{
                        wine.getWineId(),
                        wine.getName(),
                        wine.getYear(),
                        wine.getPrice(),
                        wine.getSweetnessLevel(),
                        wine.getProductImage(),
                        wine.getDescription(),
                        wine.getCatalogNumber()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving wines: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exports wines to an XML file.
     */
    private void exportToXML() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Set to FILES_ONLY mode
        fileChooser.setDialogTitle("Save XML File");

        // Suggest a default file name
        fileChooser.setSelectedFile(new File("wine_producers.xml"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String userSelectedPath = fileChooser.getSelectedFile().getAbsolutePath();  // Get the full file path

            try {
                // Get wine producers
                List<WineProducer> producers = new WineProducerDAO().getAllWineProducers();

                // Save to user-selected location
                new WineImportController().exportToXml(producers, userSelectedPath);
                System.out.println("File saved to user-selected location: " + userSelectedPath);

                // Save to Eclipse project folder
                String projectPath = System.getProperty("user.dir"); // Current project directory
                String eclipsePath = projectPath + "/resources/wine_producers.xml";
                File eclipseFile = new File(eclipsePath);

                // Ensure the directory exists
                if (!eclipseFile.getParentFile().exists()) {
                    boolean created = eclipseFile.getParentFile().mkdirs();
                    System.out.println("Resources directory created: " + created);
                }

                // Save to project folder
                new WineImportController().exportToXml(producers, eclipseFile.getAbsolutePath());
                System.out.println("File saved to Eclipse project folder: " + eclipseFile.getAbsolutePath());

                // Check if file exists
                if (eclipseFile.exists()) {
                    JOptionPane.showMessageDialog(this, "Export completed successfully!\n" +
                            "File saved at:\n" + userSelectedPath + "\n" +
                            "Also saved in Eclipse project folder:\n" + eclipseFile.getAbsolutePath());
                } else {
                    JOptionPane.showMessageDialog(this, "Export failed to Eclipse folder. Please check permissions.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println("File not found in Eclipse folder: " + eclipseFile.getAbsolutePath());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error during export: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Imports wines from an XML file.
     */
    private void importFromXML() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                new WineImportController().importFromXml(filePath);
                JOptionPane.showMessageDialog(this, "Import completed successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error during import: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addWine(WineProducer producer, DefaultTableModel tableModel) {
        JTextField nameField = new JTextField(10);
        JTextField yearField = new JTextField(5);
        JTextField priceField = new JTextField(5);
        JTextField imageField = new JTextField(10);
        JTextField descriptionField = new JTextField(10);
        JTextField catalogNumberField = new JTextField(10);

        // Sweetness level dropdown (1 to 4)
        JComboBox<Integer> sweetnessComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Sweetness Level:"));
        panel.add(sweetnessComboBox);
        panel.add(new JLabel("Product Image:"));
        panel.add(imageField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Catalog Number:"));
        panel.add(catalogNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Wine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Wine wine = new Wine();
                wine.setWineId(String.valueOf(System.currentTimeMillis()/1000)); // Generate temporary ID
                wine.setName(nameField.getText().isEmpty() ? null : nameField.getText());
                wine.setYear(yearField.getText().isEmpty() ? 0 : Integer.parseInt(yearField.getText()));
                wine.setPrice(priceField.getText().isEmpty() ? 0.0 : Double.parseDouble(priceField.getText()));
                wine.setSweetnessLevel(String.valueOf(sweetnessComboBox.getSelectedItem()));
                wine.setProductImage(imageField.getText().isEmpty() ? null : imageField.getText());
                wine.setDescription(descriptionField.getText().isEmpty() ? null : descriptionField.getText());
                wine.setCatalogNumber(catalogNumberField.getText().isEmpty() ? null : catalogNumberField.getText());

                new WineDAO().insertWine(producer.getManufacturerId(), wine);  // Insert wine into DB
                tableModel.addRow(new Object[]{wine.getWineId(), wine.getName(), wine.getYear(),
                        wine.getPrice(), wine.getSweetnessLevel(), wine.getProductImage(),
                        wine.getDescription(), wine.getCatalogNumber()});
                JOptionPane.showMessageDialog(this, "Wine added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding wine: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateWine(WineProducer producer, DefaultTableModel tableModel, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wine to update.");
            return;
        }

        String wineId = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField nameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField yearField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
        JTextField priceField = new JTextField(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        JTextField imageField = new JTextField((String) tableModel.getValueAt(selectedRow, 5));
        JTextField descriptionField = new JTextField((String) tableModel.getValueAt(selectedRow, 6));
        JTextField catalogNumberField = new JTextField((String) tableModel.getValueAt(selectedRow, 7));

        JComboBox<Integer> sweetnessComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        sweetnessComboBox.setSelectedItem(Integer.parseInt((String) tableModel.getValueAt(selectedRow, 4)));

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Sweetness Level:"));
        panel.add(sweetnessComboBox);
        panel.add(new JLabel("Product Image:"));
        panel.add(imageField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Catalog Number:"));
        panel.add(catalogNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Wine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Wine wine = new Wine();
                wine.setWineId(wineId);
                wine.setName(nameField.getText());
                wine.setYear(Integer.parseInt(yearField.getText()));
                wine.setPrice(Double.parseDouble(priceField.getText()));
                wine.setSweetnessLevel(String.valueOf(sweetnessComboBox.getSelectedItem()));
                wine.setProductImage(imageField.getText());
                wine.setDescription(descriptionField.getText());
                wine.setCatalogNumber(catalogNumberField.getText());

                new WineDAO().updateWine(wine);  // Update wine in DB
                tableModel.setValueAt(wine.getName(), selectedRow, 1);
                tableModel.setValueAt(wine.getYear(), selectedRow, 2);
                tableModel.setValueAt(wine.getPrice(), selectedRow, 3);
                tableModel.setValueAt(wine.getSweetnessLevel(), selectedRow, 4);
                tableModel.setValueAt(wine.getProductImage(), selectedRow, 5);
                tableModel.setValueAt(wine.getDescription(), selectedRow, 6);
                tableModel.setValueAt(wine.getCatalogNumber(), selectedRow, 7);
                JOptionPane.showMessageDialog(this, "Wine updated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating wine: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteWine(WineProducer producer, DefaultTableModel tableModel, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wine to delete.");
            return;
        }

        String wineId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this wine?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new WineDAO().deleteWine(wineId);  // Delete wine from DB
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Wine deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting wine: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddProducerDialog() {
        JTextField fullNameField = new JTextField(10);
        JTextField contactPhoneField = new JTextField(10);
        JTextField addressField = new JTextField(10);
        JTextField emailField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Contact Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Producer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Generate a unique ManufacturerID using the current timestamp
                String manufacturerId = String.valueOf(System.currentTimeMillis()/1000);

                WineProducer producer = new WineProducer();
                producer.setManufacturerId(manufacturerId);  // Set the generated ID
                producer.setFullName(fullNameField.getText().isEmpty() ? null : fullNameField.getText());
                producer.setContactPhone(contactPhoneField.getText().isEmpty() ? null : contactPhoneField.getText());
                producer.setAddress(addressField.getText().isEmpty() ? null : addressField.getText());
                producer.setEmail(emailField.getText().isEmpty() ? null : emailField.getText());

                // Insert the new producer into the database
                new WineProducerDAO().insertWineProducer(producer);
                JOptionPane.showMessageDialog(this, "Producer added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addProducer(DefaultTableModel tableModel) {
        JTextField fullNameField = new JTextField(10);
        JTextField contactPhoneField = new JTextField(10);
        JTextField addressField = new JTextField(10);
        JTextField emailField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Contact Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Producer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Generate a unique ManufacturerID using current time in milliseconds
                String manufacturerId = String.valueOf(System.currentTimeMillis()/1000);

                WineProducer producer = new WineProducer();
                producer.setManufacturerId(manufacturerId);
                producer.setFullName(fullNameField.getText().isEmpty() ? null : fullNameField.getText());
                producer.setContactPhone(contactPhoneField.getText().isEmpty() ? null : contactPhoneField.getText());
                producer.setAddress(addressField.getText().isEmpty() ? null : addressField.getText());
                producer.setEmail(emailField.getText().isEmpty() ? null : emailField.getText());

                new WineProducerDAO().insertWineProducer(producer);  // Insert producer into DB
                tableModel.addRow(new Object[]{manufacturerId, producer.getFullName(), producer.getContactPhone(),
                        producer.getAddress(), producer.getEmail()});
                JOptionPane.showMessageDialog(this, "Producer added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates the panel to manage producers.
     */
    private JPanel createProducerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Title Label
        JLabel lblTitle = new JLabel("Producer Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Table for displaying producers
        String[] columnNames = {"Producer ID", "Name", "Contact Phone", "Address", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable producerTable = new JTable(tableModel);
        panel.add(new JScrollPane(producerTable), BorderLayout.CENTER);

        // Populate the table with producers
        populateProducerTable(tableModel);

        // Buttons for add/update/delete
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton btnAddProducer = new JButton("Add Producer");
        JButton btnUpdateProducer = new JButton("Update Selected Producer");
        JButton btnDeleteProducer = new JButton("Delete Selected Producer");
        JButton btnBack = new JButton("Back");  // Back button to return to main menu

        btnAddProducer.addActionListener(e -> addProducer(tableModel));
        btnUpdateProducer.addActionListener(e -> updateProducer(tableModel, producerTable.getSelectedRow()));
        btnDeleteProducer.addActionListener(e -> deleteProducer(tableModel, producerTable.getSelectedRow()));
        btnBack.addActionListener(e -> cardLayout.show(contentPane, "MainMenu"));

        buttonPanel.add(btnAddProducer);
        buttonPanel.add(btnUpdateProducer);
        buttonPanel.add(btnDeleteProducer);
        buttonPanel.add(btnBack);  // Add Back button to the panel

        panel.add(buttonPanel, BorderLayout.SOUTH);  // Add button panel at the bottom

        return panel;
    }
    /**
     * Populates the producer table with data from the database.
     */
    private void populateProducerTable(DefaultTableModel tableModel) {
        List<WineProducer> producers = new WineProducerDAO().getAllWineProducers();
        for (WineProducer producer : producers) {
            tableModel.addRow(new Object[]{
                    producer.getManufacturerId(),
                    producer.getFullName(),
                    producer.getContactPhone(),
                    producer.getAddress(),
                    producer.getEmail()
            });
        }
    }

    /**
     * Opens a dialog to update a selected producer.
     */
    private void updateProducer(DefaultTableModel tableModel, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a producer to update.");
            return;
        }

        String manufacturerId = (String) tableModel.getValueAt(selectedRow, 0);
        JTextField fullNameField = new JTextField((String) tableModel.getValueAt(selectedRow, 1));
        JTextField contactPhoneField = new JTextField((String) tableModel.getValueAt(selectedRow, 2));
        JTextField addressField = new JTextField((String) tableModel.getValueAt(selectedRow, 3));
        JTextField emailField = new JTextField((String) tableModel.getValueAt(selectedRow, 4));

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Contact Phone:"));
        panel.add(contactPhoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Producer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                WineProducer producer = new WineProducer();
                producer.setManufacturerId(manufacturerId);
                producer.setFullName(fullNameField.getText());
                producer.setContactPhone(contactPhoneField.getText());
                producer.setAddress(addressField.getText());
                producer.setEmail(emailField.getText());

                new WineProducerDAO().updateWineProducer(producer);
                tableModel.setValueAt(producer.getFullName(), selectedRow, 1);
                tableModel.setValueAt(producer.getContactPhone(), selectedRow, 2);
                tableModel.setValueAt(producer.getAddress(), selectedRow, 3);
                tableModel.setValueAt(producer.getEmail(), selectedRow, 4);
                JOptionPane.showMessageDialog(this, "Producer updated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes a selected producer.
     */
    private void deleteProducer(DefaultTableModel tableModel, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a producer to delete.");
            return;
        }

        String manufacturerId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this producer?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new WineProducerDAO().deleteWineProducer(manufacturerId);  // Delete producer from DB
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Producer deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting producer: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void generateReport(List<String> foodPairings, List<String> occasions, List<String> sweetnessLevels) {
        boolean hasData = false; // Track if the report contains data

        while (!hasData) {
            try {
                // Path to the compiled Jasper report (.jasper file)
                String reportPath = getClass().getClassLoader().getResource("Wine_report.jasper").getPath();

                // Create a parameter map for the report
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put("FoodPairing", foodPairings); // Pass null if no value
                parameters.put("Occasion", occasions);       // Pass null if no value
                parameters.put("SweetnessLevel", sweetnessLevels); // Pass null if no value

                // Establish a database connection
                Connection connection = DatabaseConnectionManager.getConnection();

                // Fill the report with parameters and data source
                JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, parameters, connection);

                // Check if the report contains data
                if (jasperPrint.getPages().isEmpty()) {
                    int retry = JOptionPane.showConfirmDialog(this,
                            "No data found for the selected parameters. Would you like to try again?",
                            "No Data Found", JOptionPane.YES_NO_OPTION);

                    if (retry == JOptionPane.NO_OPTION) {
                        break; // Exit the loop if the user doesn't want to retry
                    } else {
                        openReportParameterDialog(); // Allow the user to select new parameters
                        return; // Restart the method
                    }
                } else {
                    hasData = true; // Data exists, display the report
                    JasperViewer.viewReport(jasperPrint, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                break; // Exit the loop on error
            }
        }
    }

    private void openReportParameterDialog() {
        try {
            WineRecommendationService wineRecommendationService = new WineRecommendationService();
            List<String> foodPairingOptions = wineRecommendationService.fetchFoodPairings().stream()
                    .map(FoodPairing::getDishName)
                    .collect(Collectors.toList());
            List<String> occasionOptions = wineRecommendationService.fetchOccasionRecommendations().stream()
                    .map(OccasionRecommendation::getDescription)
                    .collect(Collectors.toList());
            List<String> sweetnessLevelOptions = wineRecommendationService.fetchSweetnessLevels().stream()
                    .map(SweetnessLevel::getSweetnessValue)
                    .collect(Collectors.toList());

            // Use the check box dialog for each parameter
            List<String> selectedFoodPairings = showMultiSelectionCheckBoxDialog("Select Food Pairings", foodPairingOptions);
            if(selectedFoodPairings == null) return;
            List<String> selectedOccasions = showMultiSelectionCheckBoxDialog("Select Occasions", occasionOptions);
            if(selectedOccasions == null) return;
            List<String> selectedSweetnessLevels = showMultiSelectionCheckBoxDialog("Select Sweetness Levels", sweetnessLevelOptions);
            if(selectedSweetnessLevels == null) return;

            // Pass the selected options to the report generation method
            generateReport(selectedFoodPairings, selectedOccasions, selectedSweetnessLevels);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error fetching options: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private List<String> showMultiSelectionCheckBoxDialog(String title, List<String> options) {
        JPanel panel = new JPanel(new GridLayout(0, 1)); // Dynamic layout for checkboxes

        // Create a list of checkboxes for the given options
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for (String option : options) {
            JCheckBox checkBox = new JCheckBox(option);
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }

        // Show the dialog
        int result = JOptionPane.showConfirmDialog(
                null,
                new JScrollPane(panel), // Add scrollable panel if there are many options
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if(result == JOptionPane.CANCEL_OPTION) {
            return null;
        }

        // Collect selected values if "OK" is clicked
        if (result == JOptionPane.OK_OPTION) {
            List<String> selectedOptions = new ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedOptions.add(checkBox.getText());
                }
            }
            return selectedOptions;
        }
        return Collections.emptyList(); // Return empty list if "Cancel" is clicked
    }


}