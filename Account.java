import java.sql.*;
import java.util.*;

public class Account {
	private int accountId;
	private String username;
	private String password;
	private String displayName;
	private List<Post> postHistory;

	// Sets username and password. Generates an accountId
	public Account(String username, String password){
		this.username = username;
		this.password = password;
	}


	//Connects to databases needed for program to function
	//If unable to connect to database, return null and print error message in console
	public static Connection connectToDatabase() {
		Connection c = null;

		try {
			String url = "jdbc:sqlite:database/main.db";

			//Try to connect to our databases
			c = DriverManager.getConnection(url);

			System.out.println("Connection to database was successful.");

			return c;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/* Allows user to create a post. 
		Once created, it is added to postHistory.
		It is also added to the PostManager object
	*/
	public void createPost(){
		
	}

	// Removes a specific post from postHistory
	public void removePost(Post post){

	}

	public void setDisplayName(String displayName){
		displayName = displayName;
	}

	public String getDisplayName(){
		return displayName;
	}

	public int getId(){
		return accountId;
	}
}
