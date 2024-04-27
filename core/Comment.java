package core;

import java.time.Instant;

public class Comment {
	private int commentId;
	private Account author;
	private String content;
	private int votes;
	private Instant timeStamp;

	public Comment(String author, String content){

	}

	public Account getAuthor() {
		return author;
	}

	// No setter for author because it should never change

	public String getContent(){
		return content;
	}

	public void setContent(String content){

	}

	// Increment votes by 1
	public void upvote(){

	}

	// Decrement votes by 1
	public void downvote(){

	}

	public int getId() {
		return commentId;
	}
}