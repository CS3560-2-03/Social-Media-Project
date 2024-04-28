package core;

import database.SQLiteHandler;
import gui.Main;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

public class PostManager {
	private static List<Post> postList = new ArrayList<Post>();
	private static boolean sortByVotes = false; // If False, defaults to sorting by new.
	private static boolean filterByFollowed;
	private static boolean filterByTime;
	private static int timeFilterDays;

	private static SQLiteHandler sqLiteHandler = new SQLiteHandler();
	
	//lastDisplayedID: postID of last displayed post. Used to track which posts have been displayed
	//postList should never be sorted, just appended or remade
	private static int lastDisplayedID = -1;

	// By default, sortByVotes is false, filterByTime is true, and timeFilterDays is 7.
	// Sets postList by calling fetchPosts()

	public static Post nextPost() {

		int index = getIdIndex(lastDisplayedID);

		//If the last post we got was at the end of the list, fetch more posts!
		if(index == -1 || index + 1 >= postList.size()) {
			fetchPosts();
		}

		//If we ran out of posts, do nothing
		if(index + 1 >= postList.size()) {
			return null;
		} else {
			index++;
		}

		lastDisplayedID = postList.get(index).getPostId();
		return postList.get(index);
	}

	public static int getIdIndex(int postId) {
		for(int i = 0; i < postList.size(); i++) {
			if(postId == postList.get(i).getPostId()) {
				return i;
			}
		}
		return -1;
	}

	public static void setSortByVotes(boolean value) {
		sortByVotes = value;
		clearPosts();
	}
	
	private static void clearPosts() {
		Main.clearPosts();
		postList.clear();
		lastDisplayedID = -1;
	}

	// Fetches posts based on the sort and filter options and adds them to postList
	// They are fetched from a data file
	public static void fetchPosts() {
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
}