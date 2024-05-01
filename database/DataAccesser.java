package database;

import gui.AccountCreationScreen;
import gui.Main;
import core.Account;
import core.Comment;
import core.Post;
import core.PostManager;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataAccesser {
	
	public static List<Post> fetchPostsFrom(int accountId){
		List<Post> userPosts = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DataAccesser.connectToDatabase();

	        // SQLite command to retrieve account information based on accountID
	        String query = "SELECT * FROM Post WHERE accountID = ?;";
        	PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int postId = resultSet.getInt("postID");
                String title = resultSet.getString("title");
                String embedLink = resultSet.getString("embedLink");
                String textContent = resultSet.getString("textContent");
                String timeStamp = resultSet.getString("timeStamp");
                Post post = new Post(postId, accountId, title, embedLink, textContent, timeStamp);
                userPosts.add(post);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }	catch (SQLException e) {
        	e.printStackTrace();
        }
        return userPosts;
	}
	
	
	public static ResultSet fetchPostsByDate(int limit, int offset) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		
        try {
        	connection = connectToDatabase();
        	
        	// Checks for time filter. Generates query appropriately
        	String timeFilterQuery = "";
    		if (PostManager.getFilterByTime()) {
    			String dayString = "-"+PostManager.getTimeFilterDays()+" days";
    			timeFilterQuery = " WHERE Post.TimeStamp > datetime('now', '"+dayString+"') ";
    		}
    		// Checks for user filter. Generates query appropriately
    		List<Account> userFilter = PostManager.getUserFilter();
    		String userFilterQuery="";
    		if (userFilter.size() > 0) {
    			userFilterQuery = PostManager.getFilterByTime() ? " AND " : " WHERE ";
    			userFilterQuery+="accountId IN (";
    			for (int i=0; i<userFilter.size(); i++) {
    				if (i>0) {
    					userFilterQuery+=",";
    				}
    				userFilterQuery += userFilter.get(i).getId();
    			}
    			userFilterQuery+=") ";
    		}
			
    		// Create full query
            String query = "SELECT * FROM post " +
            		timeFilterQuery + userFilterQuery +
            		"ORDER BY timeStamp DESC LIMIT " + limit +
            		" OFFSET " + offset;
            
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();
        } catch(Exception ex) {
            System.out.println("Failed to getPosts: " + ex.getMessage());
        }
        return result;
    }
	
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
		int sum = 0;
		
		try {
			// This can be made more efficient but for now it should work
			connection = connectToDatabase();
			statement = connection.prepareStatement("SELECT COUNT(*) FROM PostVote WHERE postId = ? AND value = ?");
			statement.setInt(1, postId);
			statement.setInt(2, 1);
			ResultSet upvoteSet = statement.executeQuery();
			while (upvoteSet.next()) {
				sum += upvoteSet.getInt(1);
			}
			statement = connection.prepareStatement("SELECT COUNT(*) FROM PostVote WHERE postId = ? AND value = ?");
			statement.setInt(1, postId);
			statement.setInt(2, -1);
			ResultSet downvoteSet = statement.executeQuery();
			while (downvoteSet.next()) {
				sum -= downvoteSet.getInt(1);
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
			statement = connection.prepareStatement("INSERT INTO comment( postId, accountId, content, timeStamp) VALUES(?,?,?,?)");
			statement.setInt(1, comment.getPostId());
			statement.setInt(2, comment.getAccountId());
			statement.setString(3, comment.getContent());
			statement.setString(4, Instant.now().toString());
			
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
	
	public static void uploadFollow(int followedId) {
		try {
			int followerId = Main.getCurrentAccountId();
			Connection connection = connectToDatabase();
			//Insert a new record if one doesn't exist
			String query = "INSERT INTO Follow (followerId, followedId)\n" +
					"SELECT ?, ?\n" +
					"WHERE NOT EXISTS (\n" +
					"    SELECT 1 FROM Follow WHERE followerId = ? AND followedId = ?\n" +
					");";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setInt(1, followerId);
			statement.setInt(2, followedId);
			statement.setInt(3, followerId);
			statement.setInt(4, followedId);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeFollow(int followedId) {
		try {
			int followerId = Main.getCurrentAccountId();
		    Connection connection = connectToDatabase();
		    String query = "DELETE FROM Follow WHERE followerId = ? AND followedId = ?";

		    PreparedStatement statement = connection.prepareStatement(query);
		    statement.setInt(1, followerId);
		    statement.setInt(2, followedId); // Set postId
		    statement.executeUpdate();
		    statement.close();
		    connection.close();
		} catch (SQLException e) {
		    e.printStackTrace();
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
			resultSet.close();
			statement.close();
			connection.close();
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
			
			int result = 0;
			while (resultSet.next()) {
				result += resultSet.getInt("value");
			}
			resultSet.close();
			statement.close();
			connection.close();
			return result;
		} catch(Exception ex) {
			System.out.println("unable to get Post Vote: " + ex.getMessage() + "\n" + ex.getStackTrace());
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
