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

	// This constructor is for when loading existing comments
	public Comment(int commentId, int accountId, int postId, String content, Instant timeStamp){
		this.commentId = commentId;
		this.accountId = accountId;
		this.postId = postId;
		this.content = content;
		this.votes = DataAccesser.fetchCommentVotes(commentId);
		this.timeStamp = timeStamp;
		
		author = DataAccesser.fetchAccount(accountId);
	}
	
	// This constructor is for when creating new comments
	public Comment(int accountId, int postId, String content) {
		this.accountId = accountId;
		this.postId = postId;
		this.content = content;
		this.timeStamp = Instant.now();
	}
	
	// This should be called externally when creating new comments
	public void setCommentId(int commentId) {
		this.commentId = commentId;
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

	public int getCommentId() {
		return commentId;
	}
	
	public int getPostId() {
		return postId;
	}
	
	public int getAccountId() {
		return accountId;
	}

	public int refetchVotes() {
		votes = DataAccesser.fetchCommentVotes(commentId);
		return votes;
	}

	public int getVotes() {
		return votes;
	}
}