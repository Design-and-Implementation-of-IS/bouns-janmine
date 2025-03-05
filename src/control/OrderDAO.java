package control;

import model.Order;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderDAO {

    // Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT OrderID, OrderNumber, OrderDate, OrderStatusID, ShipmentDate, AssignedEmployeeID, DeliveryID, PaymentStatus FROM [ORDER]";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int orderId = rs.getInt("OrderID");
                String orderNumber = rs.getString("OrderNumber");
                Date orderDate = rs.getDate("OrderDate");
                int orderStatusId = rs.getInt("OrderStatusID");
                Date shipmentDate = rs.getDate("ShipmentDate");
                String assignedEmployeeId = rs.getString("AssignedEmployeeID");
                String deliveryId = rs.getString("DeliveryID");
                String paymentStatus = rs.getString("PaymentStatus");

                orders.add(new Order(orderId, orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId, deliveryId, paymentStatus));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean addOrder(String orderNumber, Date orderDate, int orderStatusId, Date shipmentDate, String assignedEmployeeId) {
        String query = "INSERT INTO [ORDER] (OrderID, OrderNumber, OrderDate, OrderStatusID, ShipmentDate, AssignedEmployeeID) VALUES (?, ?, ?, ?, ?, ?)";

        long generatedOrderId = System.currentTimeMillis() / 1000; // Generate ID using timestamp

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setLong(1, generatedOrderId); // Use timestamp as OrderID
            pstmt.setString(2, orderNumber);
            pstmt.setDate(3, new java.sql.Date(orderDate.getTime()));
            pstmt.setInt(4, orderStatusId);
            pstmt.setDate(5, shipmentDate != null ? new java.sql.Date(shipmentDate.getTime()) : null);
            pstmt.setString(6, assignedEmployeeId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Order added with OrderID: " + generatedOrderId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        String query = "UPDATE \"Order\" SET OrderStatusID = (SELECT OrderStatusID FROM OrderStatus WHERE StatusValue = ?) " +
                "WHERE OrderID = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignOrderToDelivery(int orderId, String deliveryId) {
        String checkBottleCountQuery = "SELECT SUM(oi.Quantity) FROM OrderItem oi WHERE oi.OrderID = ?";
        String getDeliveryDetailsQuery = "SELECT BottleCount, MinBottles, MaxBottles FROM Delivery WHERE DeliveryID = ?";
        String updateBottleCountQuery = "UPDATE Delivery SET BottleCount = ? WHERE DeliveryID = ?";
        String assignOrderQuery = "UPDATE [Order] SET DeliveryID = ? WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement checkBottleCountStmt = connection.prepareStatement(checkBottleCountQuery);
             PreparedStatement getDeliveryDetailsStmt = connection.prepareStatement(getDeliveryDetailsQuery);
             PreparedStatement updateBottleCountStmt = connection.prepareStatement(updateBottleCountQuery);
             PreparedStatement assignOrderStmt = connection.prepareStatement(assignOrderQuery)) {

            // Get total bottles in the order
            checkBottleCountStmt.setInt(1, orderId);
            ResultSet orderBottleResult = checkBottleCountStmt.executeQuery();
            int orderBottleCount = orderBottleResult.next() ? orderBottleResult.getInt(1) : 0;

            // Get delivery bottle count, min & max limits
            getDeliveryDetailsStmt.setString(1, deliveryId);
            ResultSet deliveryResult = getDeliveryDetailsStmt.executeQuery();
            if (!deliveryResult.next()) return false; // Delivery not found

            int currentBottleCount = deliveryResult.getInt("BottleCount");
            int minBottles = deliveryResult.getInt("MinBottles");
            int maxBottles = deliveryResult.getInt("MaxBottles");

            int newBottleCount = currentBottleCount + orderBottleCount;

            // Validate bottle count is within range
            if (newBottleCount > maxBottles) {
                JOptionPane.showMessageDialog(null, "⚠️ Cannot assign order! Exceeds maximum bottle limit.");
                return false;
            }

            // Update bottle count in delivery
            updateBottleCountStmt.setInt(1, newBottleCount);
            updateBottleCountStmt.setString(2, deliveryId);
            updateBottleCountStmt.executeUpdate();

            // Assign order to delivery
            assignOrderStmt.setString(1, deliveryId);
            assignOrderStmt.setInt(2, orderId);
            return assignOrderStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update order shipment date
    public boolean updateShipmentDate(int orderId, Date shipmentDate) {
        String query = "UPDATE [ORDER] SET ShipmentDate = ? WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setDate(1, new java.sql.Date(shipmentDate.getTime()));
            pstmt.setInt(2, orderId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}