package control;

import model.Delivery;
import model.Order;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO {

    // Get all deliveries
    public List<Delivery> getAllDeliveries() {
        List<Delivery> deliveries = new ArrayList<>();
        String query = "SELECT DeliveryID, CityName, Status, DispatchDate, BottleCount, MaxBottles, MinBottles FROM Delivery";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String deliveryId = rs.getString("DeliveryID");
                String cityName = rs.getString("CityName");
                String status = rs.getString("Status");
                Date dispatchDate = rs.getDate("DispatchDate");
                int bottleCount = rs.getInt("BottleCount");
                int maxBottles = rs.getInt("MaxBottles");
                int minBottles = rs.getInt("MinBottles");

                deliveries.add(new Delivery(deliveryId, cityName, status, dispatchDate, bottleCount, maxBottles, minBottles, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deliveries;
    }

    public Delivery getDeliveryById(String deliveryId) {
        String query = "SELECT * FROM Delivery WHERE DeliveryID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, deliveryId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new Delivery(
                        rs.getString("DeliveryID"),
                        rs.getString("CityName"),
                        rs.getString("Status"),
                        rs.getTimestamp("DispatchDate"),
                        rs.getInt("BottleCount"),
                        rs.getInt("MinBottles"),
                        rs.getInt("MaxBottles"),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateDelivery(String deliveryId, String cityName, Integer minBottles, Integer maxBottles, String status) {
        String query = "UPDATE Delivery SET " +
                "CityName = COALESCE(?, CityName), " +
                "MinBottles = COALESCE(?, MinBottles), " +
                "MaxBottles = COALESCE(?, MaxBottles), " +
                "Status = COALESCE(?, Status), " +
                "DispatchDate = CASE WHEN ? = 'Dispatched' THEN CURRENT_TIMESTAMP ELSE DispatchDate END " +
                "WHERE DeliveryID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, cityName);
            statement.setObject(2, minBottles);
            statement.setObject(3, maxBottles);
            statement.setString(4, status);
            statement.setString(5, status);  // Used again for DispatchDate logic
            statement.setString(6, deliveryId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToDeliveryBottleCount(String deliveryId, int bottleCount) {
        String updateQuery = "UPDATE Delivery SET BottleCount = BottleCount + ? WHERE DeliveryID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, bottleCount);
            updateStmt.setString(2, deliveryId);
            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateDeliveryStatus(String deliveryId, String newStatus) {
        String getBottleCountQuery = "SELECT BottleCount, MinBottles, MaxBottles FROM Delivery WHERE DeliveryID = ?";
        String updateStatusQuery = "UPDATE Delivery SET Status = ? WHERE DeliveryID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement getBottleCountStmt = connection.prepareStatement(getBottleCountQuery);
             PreparedStatement updateStatusStmt = connection.prepareStatement(updateStatusQuery)) {

            // Get bottle count and min/max limits
            getBottleCountStmt.setString(1, deliveryId);
            ResultSet rs = getBottleCountStmt.executeQuery();
            if (!rs.next()) return false; // Delivery not found

            int bottleCount = rs.getInt("BottleCount");
            int minBottles = rs.getInt("MinBottles");
            int maxBottles = rs.getInt("MaxBottles");

            // Validate before updating status
            if (bottleCount < minBottles) {
                JOptionPane.showMessageDialog(null, "⚠️ Cannot update status! Not enough bottles for delivery.");
                return false;
            }

            updateStatusStmt.setString(1, newStatus);
            updateStatusStmt.setString(2, deliveryId);
            return updateStatusStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addDelivery(String cityName, int minBottles, int maxBottles) {
        String query = "INSERT INTO Delivery (DeliveryID, CityName, Status, DispatchDate, BottleCount, MinBottles, MaxBottles) " +
                "VALUES (?, ?, 'Pending', NULL, 0, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            String deliveryId = Long.toString(System.currentTimeMillis() / 1000);
            stmt.setString(1, deliveryId);
            stmt.setString(2, cityName);
            stmt.setInt(3, minBottles);
            stmt.setInt(4, maxBottles);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean cancelDelivery(String deliveryId) {
        String unassignOrdersQuery = "UPDATE [Order] SET DeliveryID = NULL WHERE DeliveryID = ?";
        String updateQuery = "UPDATE Delivery SET Status = 'Cancelled' WHERE DeliveryID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement unassignStmt = connection.prepareStatement(unassignOrdersQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            // Step 1: Unassign all orders from this delivery
            unassignStmt.setString(1, deliveryId);
            unassignStmt.executeUpdate();

            // Step 2: Mark the delivery as 'Cancelled'
            updateStmt.setString(1, deliveryId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all orders assigned to a specific delivery
    public List<Order> getOrdersByDelivery(String deliveryId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT OrderID, OrderNumber, OrderDate, OrderStatusID, ShipmentDate, AssignedEmployeeID, DeliveryID, PaymentStatus FROM [ORDER] WHERE DeliveryID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, deliveryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("OrderID");
                String orderNumber = rs.getString("OrderNumber");
                Date orderDate = rs.getDate("OrderDate");
                int orderStatusId = rs.getInt("OrderStatusID");
                Date shipmentDate = rs.getDate("ShipmentDate");
                String assignedEmployeeId = rs.getString("AssignedEmployeeID");
                String paymentStatus = rs.getString("PaymentStatus");

                orders.add(new Order(orderId, orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId, deliveryId, paymentStatus));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public String getDeliveryStatus(String deliveryId) {
        String query = "SELECT Status FROM Delivery WHERE DeliveryID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, deliveryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}