package core;

import java.time.Instant;
import java.util.*;

public class Post {
	private int postId;
	private String title;
	private String textContent;
	private String embedLink;
	private int votes;
	private Instant timeStamp;
	private Account author;
	private List<Comment> comments;

	// Create post with no embedded image. Generates a postId
	public Post(int postID, int authorID, String title, String textContent, int votes, String timeStamp){

	}

	// Create post with embedded image. Generates a postId
	public Post(String author, String title, String textContent, String embedLink){

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
		return 0;
	}
	// votes has no setter because it should only be modified by upvote() or downvote()

	public String getTextContent(){
		return null;
	}

	public void setTextContent(String content){

	}

	public String getTitle(){
		return null;
	}

	public void setTitle(String title){

	}

	public String getEmbedLink(){
		return null;
	}

	public Account getAuthor(){
		return author;
	}
}