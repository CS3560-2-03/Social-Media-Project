package core;

import java.time.Instant;
import java.util.*;

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

	// Create post with no embedded image. Generates a postId
	public Post(int postId, int authorId, String title, String textContent, int votes, String timeStamp){
		this.postId = postId;
		this.authorId = authorId;
		this.title = title;
		this.textContent = textContent;
		this.votes = votes;
		try {
			this.timeStamp = Instant.parse(timeStamp);
		} catch(Exception ex) {
			System.out.println("For Post " + postId  + ": unable to parse timeStamp: " + ex.getMessage());
			this.timeStamp = Instant.now();
		}

	}



	// Expands post, displaying full content and comments
	public void expand(){

	}

	// Increment votes by 1
	public void upvote(){

	}

	// Decrement votes by 1
	public void downvote(){

	}

	// Embed link cannot be edited, only removed
	public void removeEmbed(){
		
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

	public void setTextContent(String content){

	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){

	}

	public String getTimeStamp() { return timeStamp.toString(); }

	public String getEmbedLink(){
		return null;
	}

	public Account getAuthor(){
		if(author == null) {
			return new Account(-1, "user", "pass", "fakeUser");
		}

		return author;
	}

	public void setAuthor(Account account) {
		this.author = account;
	}
}