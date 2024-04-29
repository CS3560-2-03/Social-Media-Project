package gui;
import core.Account;
import core.Comment;
import core.PostManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataAccesser {
	
	// Fetch posts, sorted by votes.
	// Also filters based on PostManager's time filter variables
	public static ResultSet fetchPostsByVote() {
		// This never gets closed so that PostManager can work with it
		// Not sure if will cause memory leak?
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		String timeFilterQuery = "";
		if (PostManager.getFilterByTime()) {
			timeFilterQuery = "WHERE Post.TimeStamp > datetime('now', ?) ";
		}
		
		try {
			connection = connectToDatabase();
			String query = "SELECT Post.*, SUM(PostVote.value) AS totalVotes " +
		               "FROM Post " +
		               "LEFT JOIN PostVote ON Post.postId = PostVote.postId " +
		               timeFilterQuery +
		               "GROUP BY Post.postId " +
		               "ORDER BY CASE WHEN totalVotes < 0 THEN 1 ELSE 0 END, totalVotes DESC";
			statement = connection.prepareStatement(query);
			if (PostManager.getFilterByTime()) {
				statement.setString(1, "-"+PostManager.getTimeFilterDays()+" days");
			}
			resultSet = statement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultSet;
	}
	
	
	public static int fetchPostVotes(int postId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int sum = 0;
		
		try {
			// This can be made more efficient but for now it should work
			connection = connectToDatabase();
			statement = connection.prepareStatement("SELECT COUNT(*) FROM PostVote WHERE postId = ? AND value = ?");
			statement.setInt(1, postId);
			statement.setInt(2, 1);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				sum += resultSet.getInt(1);
			}
			statement = connection.prepareStatement("SELECT COUNT(*) FROM PostVote WHERE postId = ? AND value = ?");
			statement.setInt(1, postId);
			statement.setInt(2, -1);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				sum -= resultSet.getInt(1);
			}
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
		return sum;
	}
	
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
