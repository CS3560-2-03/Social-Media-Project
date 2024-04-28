package core;

import database.SQLiteHandler;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

public class PostManager {


	private List<Post> postList;
	private boolean sortByVotes = false; // If False, defaults to sorting by new.
	private boolean filterByFollowed;
	private boolean filterByTime;
	private int timeFilterDays;

	//lastID: Used to track which posts have been displayed
	//postList should never be sorted, just appended or remade
	private int lastID = -1;


	private SQLiteHandler sqLiteHandler;

	// By default, sortByVotes is false, filterByTime is true, and timeFilterDays is 7.
	// Sets postList by calling fetchPosts()
	public PostManager(){
		sqLiteHandler = new SQLiteHandler();
		postList = new ArrayList<Post>();
	}

	public Post nextPost() {

		int index = getIdIndex(lastID);

		//If the last post we got was at the end of the list
		//fetch more posts!
		if(index == -1 || index + 1 >= postList.size()) {
			fetchPosts();
		}

		//If we ran out of posts, loop back to beginning of list
		if(index + 1 >= postList.size()) {
			index = 0;

			//We could also fill it with dummy posts here:
			//return new Post(-1, -1, "Lorem Ipsum", "<html>Lorem ipsum dolor sit amet, consectetur adipiscing elit. In mollis lorem id justo cursus, nec congue purus commodo. Sed ut enim eros. Proin dignissim metus metus, ac tempor sapien blandit quis. Sed ac faucibus nunc. Etiam ullamcorper velit sit amet massa lacinia aliquam. Sed eget fermentum leo, sed maximus libero. Quisque cursus elit turpis, id egestas leo pretium quis.</html>", 0,  Instant.now().toString());
		} else {
			index++;
		}

		lastID = postList.get(index).getPostId();
		return postList.get(index);
	}

	public int getIdIndex(int postId) {
		for(int i = 0; i < postList.size(); i++) {
			if(postId == postList.get(i).getPostId()) {
				return i;
			}
		}
		return -1;
	}


	// Fetches posts based on the sort and filter options and adds them to postList
	// They are fetched from a data file
	public void fetchPosts() {
		//todo: fetchPosts should get NEW posts, not the same lol
		ResultSet result;
		sqLiteHandler.startConnection();
		if(sortByVotes) {
			result = sqLiteHandler.getPostsByVote(Constants.POST_GRAB_SIZE, postList.size());
		} else {
			result = sqLiteHandler.getPostsByDate(Constants.POST_GRAB_SIZE, postList.size());
		}
		if(result == null) {
			System.out.println("Error fetching posts");
			return;
		}

		//Try to add posts to postList
		int id, accountID, votes;
		String title, textContent, timeStamp;
		try {
			boolean alreadyExists = false;
			while(result.next()) {
				try {
					//Check if this post is already instantiated
					alreadyExists = false;
					id = result.getInt("postID");

					for(Post p : postList) {
						if(p.getPostId() == id) {
							alreadyExists = true;
							break;
						}
					}
					if(!alreadyExists) {
						//Grab the data from the result
						accountID = result.getInt("accountID");
						title = result.getString("title");
						textContent = result.getString("textContent");
						votes = result.getInt("votes");
						timeStamp = result.getString("timeStamp");
						//Create a new post and add it to the list
						Post post = new Post(id, accountID, title, textContent, votes, timeStamp);

						postList.add(post);

					}
				} catch (Exception ex) {
					System.out.println("Failed to parse result: " + ex.getMessage());
				}
			}
		} catch(Exception ex) {
			System.out.println("resultset was not valid: " + ex.getMessage());
		}
		sqLiteHandler.endConnection();
	}






	/*
	// Filters and/or sorts the displayed content feed.
	public void filterSortFeed(){

	}

	public void setSortByVotes(input){

	}

	public void setFilterByFollowed(input){

	}

	public void setFilterByTime(input){

	}

	public void setTimeFilterDays(days){

	}
	*/
}