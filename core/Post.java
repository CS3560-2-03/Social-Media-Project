package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Instant;
import java.util.*;

import gui.DataAccesser;

public class Post {
	private int postId;
	private int authorId;
	private String title;
	private String textContent;
	private String embedLink;
	private int votes;
	private Instant timeStamp;
	private Account author;
	private List<Comment> comments;

	// Create post with no embedded image
	public Post(int postId, int authorId, String title, String textContent, String timeStamp){
		this.postId = postId;
		this.authorId = authorId;
		this.title = title;
		this.textContent = textContent;
		this.votes = DataAccesser.fetchPostVotes(postId);
		
		try {
			this.timeStamp = Instant.parse(timeStamp);
		} catch(Exception ex) {
			System.out.println("For Post " + postId  + ": unable to parse timeStamp: " + ex.getMessage());
			this.timeStamp = Instant.now();
		}
		this.author = DataAccesser.fetchAccount(authorId);
	}
	
	// Create post with embedded image
		public Post(int postId, int authorId, String title, String embedLink, String textContent, String timeStamp){
			this(postId, authorId, title, textContent, timeStamp);
			this.embedLink = embedLink;
		}

	//Connects to databases needed for program to function
  	private Connection connectToDatabase() {
  		Connection c = null;
  		try {
  			String url = "jdbc:sqlite:database/main.db";
  			c = DriverManager.getConnection(url);
  			System.out.println("Connection to database was successful.");
  			return c;
  		} catch (Exception e) {
  			System.out.println(e.getMessage());
  			return null;
  		}
  	}

	// Embed link cannot be edited, only removed
	public void removeEmbed(){
		
	}

	public int refetchVotes() {
		votes = DataAccesser.fetchPostVotes(postId);
		return votes;
	}


	public int getPostId() {
		return postId;
	}

	public int getVotes(){
		return votes;
	}
	// votes has no setter because it should only be modified by upvote() or downvote()

	public String getTextContent(){
		return textContent;
	}

	public String getTitle(){
		return title;
	}

	public String getTimeStamp() { 
		return timeStamp.toString(); 
	}

	public String getEmbedLink(){
		return embedLink;
	}

	public Account getAuthor(){
		return author;
	}

	public void setAuthor(Account account) {
		this.author = account;
	}
}