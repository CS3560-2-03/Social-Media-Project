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