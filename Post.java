import java.util.*;

public class Post {
	private int postId;
	private String title;
	private String textContent;
	private String embedLink;
	private int votes;
	private List<Comment> comments;

	// Create post with no embedded image. Generates a postId
	public Post(title, textContent){

	}

	// Create post with embedded image. Generates a postId
	public Post(title, textContent, embedLink){

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

	public int getVotes(){

	}
	// votes has no setter because it should only be modified by upvote() or downvote()

	public String getTextContent(){

	}

	public void setTextContent(content){

	}

	public String getTitle(){

	}

	public void setTitle(title){

	}

	public String getEmbedLink(){

	}
}