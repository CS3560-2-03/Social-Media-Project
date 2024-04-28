package gui;
import core.Account;
import core.Comment;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataAccesser {
	
	public static void uploadComment(Comment comment) {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = connectToDatabase();
			statement = connection.prepareStatement("INSERT INTO comment(postId, accountId, content, votes, timeStamp) VALUES(?,?,?,?,?)");
			statement.setInt(1, comment.getPostId());
			statement.setInt(2, comment.getAccountId());
			statement.setString(3, comment.getContent());
			statement.setInt(4, comment.getVotes());
			statement.setString(5, Instant.now().toString());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	        // Close resources in the reverse order
	        try {
	            if (statement != null) statement.close();
	            if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	// Returns an array of comments based on a postId
	public static List<Comment> fetchComments(int postId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<Comment> result = new ArrayList<>();
		
		try {
			connection = connectToDatabase();
			statement = connection.prepareStatement("SELECT * FROM comment WHERE postId = ?");
			statement.setInt(1, postId);
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				int commentId = resultSet.getInt("commentId");
				int accountId = resultSet.getInt("accountId");
				String content = resultSet.getString("content");
				Instant timeStmp = Instant.parse(resultSet.getString("timeStamp"));
				result.add(new Comment(commentId, accountId, postId, content, timeStmp));
			}
			
		} catch (SQLException e){
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
