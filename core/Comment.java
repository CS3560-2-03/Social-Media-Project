package core;

import java.time.Instant;

import gui.DataAccesser;

public class Comment {
	private int commentId;
	private int accountId;
	private int postId;
	private Account author;
	private String content;
	private int votes;
	private Instant timeStamp;

	public Comment(int commentId, int accountId, int postId, String content, Instant timeStamp){
		this.commentId = commentId;
		this.accountId = accountId;
		this.postId = postId;
		this.content = content;
		votes = 0;
		this.timeStamp = timeStamp;
		
		author = DataAccesser.fetchAccount(accountId);
	}

	public Account getAuthor() {
		return author;
	}

	public String getContent(){
		return content;
	}
	
	public Instant getTimeStamp() {
		return timeStamp;
	}

	public int getId() {
		return commentId;
	}
	
	public int getVotes() {
		return votes;
	}
}