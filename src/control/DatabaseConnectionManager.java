package control;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Wine;

import java.net.URLDecoder;

public class DatabaseConnectionManager {

	public static Connection getConnection() throws SQLException {
        try {
            // Load the database from a persistent location on disk
            URL dbUrl = DatabaseConnectionManager.class.getResource("/janmin.accdb");
            if (dbUrl == null) {
                throw new IllegalStateException("Database file not found");
            }

            File dbFile = new File(dbUrl.toURI());
            if (!dbFile.exists()) {
                throw new IllegalStateException("Database file does not exist at " + dbFile.getAbsolutePath());
            }

            // Return the connection using the persistent database file
            return DriverManager.getConnection("jdbc:ucanaccess://" + dbFile.getAbsolutePath());

        } catch (URISyntaxException e) {
            throw new SQLException("Failed to load database file", e);
        }
    }
	
	
	

	protected static final String DB_FILEPATH = getDBPath();
	public static final String CONN_STR = "jdbc:ucanaccess://" + DB_FILEPATH + ";COLUMNORDER=DISPLAY";
	
	
	/*----------------------------------------- EMPLOYEES QUERIES -----------------------------------------*/
	public static final String SQL_SEL_EMPLOYEES = "SELECT * FROM TblEmployees";
	public static final String SQL_DEL_EMPLOYEE = "{ call qryDelEmployee(?) }";
	public static final String SQL_INS_EMPLOYEE = "{ call qryInsEmployee(?,?,?,?,?,?,?,?,?,?,?) }";
	public static final String SQL_UPD_EMPLOYEE = "{ call qryUpdEmployee(?,?,?,?,?,?,?,?,?,?,?,?) }";

	/*----------------------------------------- ORDERS QUERIES --------------------------------------------*/
	public static final String SQL_SEL_ORDERS = "SELECT * FROM TblOrders";
	public static final String SQL_UPD_ORDER = "{ call qryUpdOrder(?,?,?,?,?,?,?,?,?,?) }";
	public static final String SQL_ADD_ORDER = "{ call qryInsOrder(?,?,?,?,?,?,?,?,?) }";
	public static final String SQL_DEL_ORDER = "{ call qryDelOrder(?) }";

	/*----------------------------------------- ORDERS DETAILS QUERIES ------------------------------------*/
	public static final String SQL_DEL_ORDER_DETAILS = "{ call qryDelOrderDetails(?) }";
	public static final String SQL_DEL_ORDER_DETAILS_PRODUCT = "{ call qryDelOrderDetailProduct(?,?) }";
	public static final String SQL_SEL_ORDER_DETAILS = "SELECT TblOrderDetails.orderID,TblOrderDetails.ProductID, TblProducts.ProductName, TblOrderDetails.Quantity, "
			+ "TblOrderDetails.Discount, TblProducts.UnitPrice, [TblProducts].[UnitPrice]*[TblOrderDetails].[Quantity]*(1-[TblOrderDetails].[Discount]) AS LinePrice "
			+ "FROM TblProducts INNER JOIN TblOrderDetails ON TblProducts.ProductID = TblOrderDetails.ProductID WHERE (((TblOrderDetails.OrderID)=?));";
	public static final String SQL_UPD_ORDER_DETAILS = "{ call qryUpdOrderDetails(?,?,?,?) }";
	public static final String SQL_INS_ORDER_DETAILS = "{ call qryInsOrderDetails(?,?,?,?) }";

	/*----------------------------------------- MORE QUERIES ----------------------------------------------*/
	public static final String SQL_SEL_CUSTOMERS = "SELECT TblCustomers.CustomerID, TblCustomers.CompanyName FROM TblCustomers;";
	public static final String SQL_SEL_SHIPPERS = "SELECT TblShippers.* FROM TblShippers;";
	public static final String SQL_SEL_PRODUCTS = "SELECT TblProducts.* FROM TblProducts;";

	/**
	 * find the correct path of the DB file
     * @return the path of the DB file (from eclipse or with runnable file)
	 */
	private static String getDBPath() {
		try {
			String path = DatabaseConnectionManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decoded = URLDecoder.decode(path, "UTF-8");
			// System.out.println(decoded) - Can help to check the returned path
			if (decoded.contains(".jar")) {
				decoded = decoded.substring(0, decoded.lastIndexOf('/'));
				return decoded + "/database/Tirgul05_north2000.accdb";
			} else {
				decoded = decoded.substring(0, decoded.lastIndexOf("bin/"));
				System.out.println(decoded);
				return decoded + "src/entity/Tirgul05_north2000.accdb";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void addWine(String name, String region, int year, double price, String sweetnessLevel) throws SQLException {
	    String sql = "INSERT INTO Wine (Name, Region, Year, Price, SweetnessLevel) VALUES (?, ?, ?, ?, ?)";
	    try (Connection connection = getConnection();
	         PreparedStatement statement = connection.prepareStatement(sql)) {
	        
	        // Handle null or empty values
	        if (name == null || name.trim().isEmpty()) {
	            name = "Unknown";
	        }
	        if (region == null || region.trim().isEmpty()) {
	            region = "Unknown";
	        }
	        if (sweetnessLevel == null || sweetnessLevel.trim().isEmpty()) {
	            sweetnessLevel = "Unknown";
	        }

	        // Set parameters
	        statement.setString(1, name);
	        statement.setString(2, region);
	        statement.setInt(3, year);
	        statement.setDouble(4, price);
	        statement.setString(5, sweetnessLevel);

	        // Execute the insert
	        statement.executeUpdate();
	        System.out.println("Wine added successfully!");
	    }
	}


	
	public void updateWine(int wineID, String name, String region, int year, double price, String sweetnessLevel) throws SQLException {
	    String sql = "UPDATE Wines SET Name = ?, Region = ?, Year = ?, Price = ?, SweetnessLevel = ? WHERE WineID = ?";
	    try (Connection connection = getConnection();
	         PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setString(1, name);
	        statement.setString(2, region);
	        statement.setInt(3, year);
	        statement.setDouble(4, price);
	        statement.setString(5, sweetnessLevel);
	        statement.setInt(6, wineID);
	        statement.executeUpdate();
	    }
	}

	public void deleteWine(int wineID) throws SQLException {
	    String sql = "DELETE FROM Wines WHERE WineID = ?";
	    try (Connection connection = DatabaseConnectionManager.getConnection();
	         PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, wineID);
	        statement.executeUpdate();
	    }
	}
		
		public List<Wine> getAllWines() throws SQLException {
			String sql = "SELECT * FROM Wine";
		    List<Wine> wines = new ArrayList<>();
		    try (Connection connection = DatabaseConnectionManager.getConnection();
		         PreparedStatement statement = connection.prepareStatement(sql);
		         ResultSet resultSet = statement.executeQuery()) {

		        while (resultSet.next()) {
		            String wineId = resultSet.getString("WineID");
		            String name = resultSet.getString("Name");
		            int productionYear = resultSet.getInt("ProductionYear"); // Updated column name
		            double price = resultSet.getDouble("PricePerBottle"); // Updated column name
		            String sweetnessLevel = resultSet.getString("SweetnessLevelID"); // Updated column name

		            Wine wine = new Wine(wineId, name, productionYear, price, sweetnessLevel);
		            wines.add(wine);
		        }
		    }

		    return wines;
		}}




/**
 * http://www.javapractices.com/topic/TopicAction.do?Id=2
 */



