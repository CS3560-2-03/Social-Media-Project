package core;

import database.SQLiteHandler;

import java.sql.ResultSet;
import java.util.*;

public class PostManager {


	private List<Post> postList;
	private boolean sortByVotes; // If False, defaults to sorting by new.
	private boolean filterByFollowed;
	private boolean filterByTime;
	private int timeFilterDays;

	private SQLiteHandler sqLiteHandler;

	// By default, sortByVotes is false, filterByTime is true, and timeFilterDays is 7.
	// Sets postList by calling fetchPosts()
	public PostManager(){
		sqLiteHandler = new SQLiteHandler();
	}

	// Fetches posts based on the sort and filter options and adds them to postList
	// They are fetched from a data file
	public void fetchPosts() {
		ResultSet result = sqLiteHandler.getPosts(50);

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
						postList.add(new Post(id, accountID, title, textContent, votes, timeStamp));
					}
				} catch (Exception ex) {
					System.out.println("Failed to parse result: " + ex.getMessage());
				}
			}
		} catch(Exception ex) {
			System.out.println("resultset was not valid: " + ex.getMessage());
		}

	}

	// Filters and/or sorts the displayed content feed.
	public void filterSortFeed(){

	}
	/*
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