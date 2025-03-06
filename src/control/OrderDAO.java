package control;

import model.Order;
import model.OrderStatus;
import model.UrgentOrder;

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

    public List<Order> getRegularOrders() {
        String query = "SELECT o.OrderID, o.OrderNumber, o.OrderDate, o.OrderStatusID, o.ShipmentDate, " +
                "o.AssignedEmployeeID, o.DeliveryID, o.PaymentStatus " +
                "FROM [Order] o " +
                "JOIN RegularOrder r ON o.OrderID = r.OrderID";

        List<Order> orders = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("OrderID"),
                        rs.getString("OrderNumber"),
                        rs.getTimestamp("OrderDate"),
                        rs.getInt("OrderStatusID"),
                        rs.getTimestamp("ShipmentDate"),
                        rs.getString("AssignedEmployeeID"),
                        rs.getString("DeliveryID"),
                        rs.getString("PaymentStatus")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<UrgentOrder> getUrgentOrders() {
        String query = "SELECT o.OrderID, o.OrderNumber, o.OrderDate, o.OrderStatusID, o.ShipmentDate, " +
                "o.AssignedEmployeeID, o.DeliveryID, o.PaymentStatus, " +
                "u.Priority, u.ExpectedDeliveryTime " +
                "FROM [Order] o " +
                "JOIN UrgentOrder u ON o.OrderID = u.OrderID";

        List<UrgentOrder> orders = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UrgentOrder order = new UrgentOrder(
                        rs.getInt("OrderID"),
                        rs.getString("OrderNumber"),
                        rs.getTimestamp("OrderDate"),
                        rs.getInt("OrderStatusID"),
                        rs.getTimestamp("ShipmentDate"),
                        rs.getString("AssignedEmployeeID"),
                        rs.getString("DeliveryID"),
                        rs.getString("PaymentStatus"),
                        rs.getInt("Priority"),
                        rs.getTimestamp("ExpectedDeliveryTime")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean addRegularOrder(String orderNumber, Date orderDate, int orderStatusId, Date shipmentDate, String assignedEmployeeId) {
        long orderId = addOrder(orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId);

        if (orderId > 0) {
            String query = "INSERT INTO RegularOrder (OrderID) VALUES (?)";

            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setLong(1, orderId);
                int affectedRows = pstmt.executeUpdate();

                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public long addOrder(String orderNumber, Date orderDate, int orderStatusId, Date shipmentDate, String assignedEmployeeId) {
        String query = "INSERT INTO [ORDER] (OrderID, OrderNumber, OrderDate, OrderStatusID, ShipmentDate, AssignedEmployeeID) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            long generatedOrderId = System.currentTimeMillis() / 1000;

            pstmt.setLong(1, generatedOrderId);
            pstmt.setString(2, orderNumber);
            pstmt.setDate(3, new java.sql.Date(orderDate.getTime()));
            pstmt.setInt(4, orderStatusId);
            pstmt.setDate(5, shipmentDate != null ? new java.sql.Date(shipmentDate.getTime()) : null);
            pstmt.setString(6, assignedEmployeeId);


            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return generatedOrderId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if insertion fails
    }


    public boolean addUrgentOrder(String orderNumber, Date orderDate, int orderStatusId, Date shipmentDate,
                                  String assignedEmployeeId, int priority, Date expectedDeliveryTime) {
        long orderId = addOrder(orderNumber, orderDate, orderStatusId, shipmentDate, assignedEmployeeId);

        if (orderId > 0) {
            String query = "INSERT INTO UrgentOrder (OrderID, Priority, ExpectedDeliveryTime) VALUES (?, ?, ?)";

            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setLong(1, orderId);
                pstmt.setInt(2, priority);
                pstmt.setDate(3, expectedDeliveryTime != null ? new java.sql.Date(expectedDeliveryTime.getTime()) : null);

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateOrder(int orderId, OrderStatus newStatus, Date shipmentDate, String assignedEmployeeId) {
        String query = "UPDATE [Order] SET " +
                "OrderStatusID = ?, " +
                "ShipmentDate = COALESCE(?, ShipmentDate), " +
                "AssignedEmployeeID = ? " +
                "WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, newStatus.getId());
            statement.setObject(2, shipmentDate);
            statement.setString(3, assignedEmployeeId);
            statement.setInt(4, orderId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUrgentOrder(int orderId, int priority, Date expectedDelivery) {
        String query = "UPDATE UrgentOrder SET Priority = ?, ExpectedDeliveryTime = ? WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, priority);
            statement.setObject(2, expectedDelivery);
            statement.setInt(3, orderId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void unassignOrderItems(int orderId) {
        String query = "UPDATE OrderItem SET OrderID = NULL WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean updateOrderStatus(int orderId, String newStatus, Date shipmentDate, String assignedEmployeeId, Integer priority, Date expectedDelivery) {
        if (newStatus == null && shipmentDate == null && assignedEmployeeId == null && priority == null && expectedDelivery == null) {
            return false; // Nothing to update
        }

        StringBuilder query = new StringBuilder("UPDATE [Order] SET ");
        List<Object> params = new ArrayList<>();

        if (newStatus != null) {
            query.append("OrderStatusID = (SELECT OrderStatusID FROM OrderStatus WHERE StatusValue = ?), ");
            params.add(newStatus);
        }
        if (shipmentDate != null) {
            query.append("ShipmentDate = ?, ");
            params.add(new java.sql.Date(shipmentDate.getTime()));
        }
        if (assignedEmployeeId != null) {
            query.append("AssignedEmployeeID = ?, ");
            params.add(assignedEmployeeId);
        }

        // Remove last comma and space
        query.setLength(query.length() - 2);
        query.append(" WHERE OrderID = ?");
        params.add(orderId);

        boolean orderUpdated = false;
        boolean urgentOrderUpdated = false;

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            orderUpdated = stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // If the order is urgent, update priority and expected delivery date
        if (priority != null || expectedDelivery != null) {
            StringBuilder urgentQuery = new StringBuilder("UPDATE UrgentOrder SET ");
            List<Object> urgentParams = new ArrayList<>();

            if (priority != null) {
                urgentQuery.append("Priority = ?, ");
                urgentParams.add(priority);
            }
            if (expectedDelivery != null) {
                urgentQuery.append("ExpectedDeliveryTime = ?, ");
                urgentParams.add(new java.sql.Timestamp(expectedDelivery.getTime()));
            }

            urgentQuery.setLength(urgentQuery.length() - 2);
            urgentQuery.append(" WHERE OrderID = ?");
            urgentParams.add(orderId);

            try (Connection connection = DatabaseConnectionManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(urgentQuery.toString())) {

                for (int i = 0; i < urgentParams.size(); i++) {
                    stmt.setObject(i + 1, urgentParams.get(i));
                }

                urgentOrderUpdated = stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        return orderUpdated || urgentOrderUpdated;
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