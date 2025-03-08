package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class QueryClient {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Ask me anything!");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.setLayout(new BorderLayout());

        JTextField inputField = new JTextField();
        JButton queryButton = new JButton("Answer");
        JTable resultTable = new JTable();  // JTable to show results
        JScrollPane scrollPane = new JScrollPane(resultTable);  // Scrollable table

        queryButton.addActionListener(e -> {
            String question = inputField.getText();
            if (!question.isEmpty()) {
                String response = sendQueryToAPI(question);
                updateTable(resultTable, response);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(queryButton, BorderLayout.EAST);

        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static String sendQueryToAPI(String question) {
        try {
            URL url = new URL("http://127.0.0.1:8000/query");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"question\": \"" + question + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInputString.getBytes());
                os.flush();
            }

            InputStream responseStream = conn.getInputStream();
            Scanner scanner = new Scanner(responseStream).useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch data\"}";
        }
    }

    private static void updateTable(JTable table, String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            JSONArray data = json.getJSONObject("result").getJSONArray("data");

            // Extract column names dynamically
            if (data.length() > 0) {
                JSONObject firstRow = data.getJSONObject(0);
                String[] columnNames = firstRow.keySet().toArray(new String[0]);

                // Populate table data
                Object[][] tableData = new Object[data.length()][columnNames.length];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject row = data.getJSONObject(i);
                    for (int j = 0; j < columnNames.length; j++) {
                        tableData[i][j] = row.get(columnNames[j]);
                    }
                }

                // Update JTable with new data
                table.setModel(new DefaultTableModel(tableData, columnNames));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error parsing JSON response");
        }
    }
}