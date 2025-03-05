package service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import control.DatabaseConnectionManager;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class CurrentInventoryReportService {

    public void generateInventoryReport() {
        JSONArray inventoryArray = new JSONArray();
        String query = "SELECT ir.InventoryRecordID, ir.WineID, w.Name, w.Description, w.ProductionYear, " +
                "w.PricePerBottle, ir.Quantity " +
                "FROM InventoryRecord ir " +
                "JOIN Wine w ON ir.WineID = w.WineID";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JSONObject wineEntry = new JSONObject();
                wineEntry.put("InventoryRecordID", rs.getInt("InventoryRecordID"));
                wineEntry.put("WineID", rs.getInt("WineID"));
                wineEntry.put("Name", rs.getString("Name"));
                wineEntry.put("Description", rs.getString("Description"));
                wineEntry.put("ProductionYear", rs.getInt("ProductionYear"));
                wineEntry.put("PricePerBottle", rs.getDouble("PricePerBottle"));
                wineEntry.put("Quantity", rs.getInt("Quantity"));

                inventoryArray.add(wineEntry);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching inventory data: " + e.getMessage());
        }

        writeToJsonFile(inventoryArray);
    }

    private void writeToJsonFile(JSONArray inventoryData) {
        try (FileWriter file = new FileWriter("current_inventory.json")) {
            file.write(inventoryData.toJSONString());
            file.flush();
            System.out.println("Inventory report saved successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        CurrentInventoryReportService reportService = new CurrentInventoryReportService();
        reportService.generateInventoryReport();
    }
}
