package gui;
import core.Account;

import java.sql.*;

public class DataAccesser {
	
	// Returns an account based on accountId.
	public static Account fetchAccount(int accountId) {
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
	    Account result = null;
	    
	    try {
	        connection = connectToDatabase();
	        statement = connection.prepareStatement("SELECT * FROM account WHERE accountId = ?");
	        statement.setInt(1, accountId);
	        resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            String username = resultSet.getString("username");
	            String password = resultSet.getString("password");
	            String displayName = resultSet.getString("displayName");
	            result = new Account(accountId, username, password, displayName);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        // Close resources in the reverse order
	        try {
	            if (resultSet != null) resultSet.close();
	            if (statement != null) statement.close();
	            if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
		return result;
	}
	
	
	//Connects to databases needed for program to function
  	private static Connection connectToDatabase() {
  		Connection c = null;
  		try {
  			String url = "jdbc:sqlite:database/main.db";
  			c = DriverManager.getConnection(url);
  			return c;
  		} catch (Exception e) {
  			System.out.println(e.getMessage());
  			return null;
  		}
  	}
}
