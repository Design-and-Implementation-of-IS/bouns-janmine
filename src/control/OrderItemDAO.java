package control;

import model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    public boolean addOrderItem(int orderId, int wineId, int quantity, double unitPrice) {
        String query = "INSERT INTO OrderItem (OrderItemID,OrderID, WineID, Quantity, UnitPrice) VALUES (?, ?, ?, ?, ?)";
        long generatedOrderItemId = System.currentTimeMillis() / 1000;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, generatedOrderItemId);
            stmt.setInt(2, orderId);
            stmt.setInt(3, wineId);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, unitPrice);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT OrderItemID, OrderID, WineID, Quantity, UnitPrice FROM OrderItem WHERE OrderID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem orderItem = new OrderItem();
                        orderItem.setOrderItemId(rs.getInt("OrderItemID"));
                        orderItem.setOrderId(rs.getInt("OrderID"));
                        orderItem.setQuantity(rs.getInt("Quantity"));
                        orderItem.setWineId(rs.getInt("WineID"));
                        orderItem.setUnitPrice(rs.getDouble("UnitPrice"));
                orderItems.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public boolean updateOrderItem(int orderItemId, int quantity, double unitPrice) {
        String query = "UPDATE OrderItem SET Quantity = ?, UnitPrice = ? WHERE OrderItemID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, quantity);
            stmt.setDouble(2, unitPrice);
            stmt.setInt(3, orderItemId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateOrderItemQuantity(int orderItemId, int quantity, int wineId) {
        String query = "UPDATE OrderItem SET Quantity = ? WHERE OrderItemID = ? AND wineId = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, orderItemId);
            stmt.setInt(3, wineId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOrderItem(int orderItemId) {
        String query = "DELETE FROM OrderItem WHERE OrderItemID = ?";

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, orderItemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
