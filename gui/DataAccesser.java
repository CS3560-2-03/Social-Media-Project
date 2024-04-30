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
			String dayString = "-"+PostManager.getTimeFilterDays()+" days";
			timeFilterQuery = "WHERE Post.TimeStamp > datetime('now', '"+dayString+"') ";
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
			resultSet = statement.executeQuery();
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return resultSet;
	}
	
	public static int fetchCommentVotes(int commentId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int sum = 0;
		
		try {
			// This can be made more efficient but for now it should work
			connection = connectToDatabase();
			statement = connection.prepareStatement("SELECT COUNT(*) FROM CommentVote WHERE commentId = ? AND value = ?");
			statement.setInt(1, commentId);
			statement.setInt(2, 1);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				sum += resultSet.getInt(1);
			}
			statement = connection.prepareStatement("SELECT COUNT(*) FROM CommentVote WHERE commentId = ? AND value = ?");
			statement.setInt(1, commentId);
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
	
	public static List<Account> fetchFollowing(int accountId) {
		List<Account> followedUsers = new ArrayList<>();
		try {
			Connection connection = connectToDatabase();
			String query = "SELECT * FROM Follow WHERE followerId = ? ORDER BY followedId DESC";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, accountId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int followedId = resultSet.getInt("followedId");
				followedUsers.add(DataAccesser.fetchAccount(followedId));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return followedUsers;
	}
	
	// Returns an array of comments based on a postId
	// Sorts them based on votes
	public static List<Comment> fetchComments(int postId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<Comment> result = new ArrayList<>();
		
		try {
			connection = connectToDatabase();
			String query = "SELECT Comment.*, SUM(CommentVote.value) AS totalVotes " +
		               "FROM Comment " +
		               "LEFT JOIN CommentVote ON Comment.CommentId = CommentVote.CommentId " +
		               "WHERE Comment.postId = ? " +
		               "GROUP BY Comment.CommentId " +
		               "ORDER BY CASE WHEN totalVotes < 0 THEN 1 ELSE 0 END, totalVotes DESC";
			statement = connection.prepareStatement(query);
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
	            String displayName = resultSet.getString("displayName");
	            result = new Account(accountId, username, displayName);
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
	
	public static void removePostVote(int accountId, int postId) {
		try {
		    Connection connection = connectToDatabase();
		    String query = "DELETE FROM PostVote WHERE accountId = ? AND postId = ?;";

		    PreparedStatement statement = connection.prepareStatement(query);
		    statement.setInt(1, accountId); // Set accountId
		    statement.setInt(2, postId); // Set postId
		    statement.executeUpdate();
		    statement.close();
		    connection.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	public static void removeCommentVote(int accountId, int commentId) {
		try {
		    Connection connection = connectToDatabase();
		    String query = "DELETE FROM CommentVote WHERE accountId = ? AND commentId = ?;";

		    PreparedStatement statement = connection.prepareStatement(query);
		    statement.setInt(1, accountId); // Set accountId
		    statement.setInt(2, commentId); // Set postId
		    statement.executeUpdate();
		    statement.close();
		    connection.close();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}


	public static void uploadPostVote(int accountId, int postId, int newValue) {
		Connection connection = null;
		PreparedStatement statement = null;
		
		try {
			connection = connectToDatabase();
			String query = "UPDATE PostVote SET value = ? WHERE accountId = ? AND postId = ?;";

			statement = connection.prepareStatement(query);
			statement.setInt(1, newValue);
			statement.setInt(2, accountId);
			statement.setInt(3, postId);
			statement.executeUpdate();
			//Insert a new record if one doesn't exist
			query = "INSERT INTO PostVote (accountId, postId, value)\n" +
					"SELECT ?, ?, ?\n" +
					"WHERE NOT EXISTS (\n" +
					"    SELECT 1 FROM PostVote WHERE accountId = ? AND postId = ?\n" +
					");";
			statement = connection.prepareStatement(query);
			statement.setInt(1, accountId);
			statement.setInt(2, postId);
			statement.setInt(3, newValue);
			statement.setInt(4, accountId);
			statement.setInt(5, postId);

			statement.executeUpdate();
		} catch(Exception ex) {
			System.out.println("Failed to add/update vote: " + ex.getMessage() + "\n" + ex.getStackTrace()[0].getLineNumber() + "\n" + ex.getCause());
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

	public static void uploadCommentVote(int accountId, int commentId, int newValue) {
		Connection connection = null;
		PreparedStatement statement = null;


		try {
			connection = connectToDatabase();

			String query = "UPDATE CommentVote SET value = ? WHERE accountId = ? AND commentId = ?;";


			statement = connection.prepareStatement(query);

			statement.setInt(1, newValue);
			statement.setInt(2, accountId);
			statement.setInt(3, commentId);

			statement.executeUpdate();

			//Insert a new record if one doesn't exist
			query = "INSERT INTO CommentVote (accountId, commentId, value)\n" +
					"SELECT ?, ?, ?\n" +
					"WHERE NOT EXISTS (\n" +
					"    SELECT 1 FROM CommentVote WHERE accountId = ? AND commentId = ?\n" +
					");";
			statement = connection.prepareStatement(query);
			statement.setInt(1, accountId);
			statement.setInt(2, commentId);
			statement.setInt(3, newValue);
			statement.setInt(4, accountId);
			statement.setInt(5, commentId);


			statement.executeUpdate();
		} catch(Exception ex) {
			System.out.println("Failed to add/update vote: " + ex.getMessage() + "\n" + ex.getStackTrace()[0].getLineNumber() + "\n" + ex.getCause());
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

	public static int getPostVote(int accountId, int postId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = connectToDatabase();
			String query = "SELECT value FROM PostVote WHERE accountId = ? AND postId = ?;";
			statement = connection.prepareStatement(query);
			statement.setInt(1, accountId);
			statement.setInt(2, postId);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int vote = resultSet.getInt("value");
				return vote;
			}

		} catch(Exception ex) {
			System.out.println("unable to get Post Vote: " + ex.getMessage() + "\n" + ex.getStackTrace());
		} finally {
	        // Close resources in the reverse order
	        try {
	        	if (resultSet != null) statement.close();
	            if (statement != null) statement.close();
	            if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}
		return 0;
	}

	public static int getCommentVote(int accountId, int commentId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = connectToDatabase();
			String query = "SELECT value FROM CommentVote WHERE accountId = ? AND commentId = ?;";
			statement = connection.prepareStatement(query);
			statement.setInt(1, accountId);
			statement.setInt(2, commentId);

			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				int vote = resultSet.getInt("value");
				return vote;
			}

		} catch(Exception ex) {
			System.out.println("unable to get Post Vote: " + ex.getMessage() + "\n" + ex.getStackTrace());
		} finally {
	        // Close resources in the reverse order
	        try {
	        	if (resultSet != null) statement.close();
	            if (statement != null) statement.close();
	            if (connection != null) connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}
		return 0;
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
