package core;

import database.DataAccesser;
import gui.Main;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.*;

public class PostManager {
	private static List<Post> postList = new ArrayList<Post>();
	private static boolean sortByVotes = false; // If False, defaults to sorting by new.
	private static boolean filterByFollowed = false;
	private static boolean filterByTime = true;
	private static int timeFilterDays = 7;
	private static List<Account> userFilter = new ArrayList<Account>();
	
	//lastDisplayedID: postID of last displayed post. Used to track which posts have been displayed
	//postList should never be sorted, just appended or remade
	private static int lastDisplayedID = -1;

	// By default, sortByVotes is false, filterByTime is true, and timeFilterDays is 7.
	// Sets postList by calling fetchPosts()
	
	public static List<Account> getUserFilter() {
		return userFilter;
	}
	public static void removeUserFilter(Account user) {
		if (userFilter.contains(user)) {
			userFilter.remove(user);
		}
		clearPosts();
	}
	public static void addUserFilter(Account user) {
		userFilter.add(user);
		clearPosts();
	}

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
	
	public static void setFilterByTime(boolean value) {
		filterByTime = value;
		clearPosts();
	}
	
	public static boolean getFilterByTime() {
		return filterByTime;
	}
	
	public static void setTimeFilterDays(int value) {
		timeFilterDays = value;
		clearPosts();
	}
	
	public static int getTimeFilterDays() {
		return timeFilterDays;
	}
	
	public static void clearPosts() {
		Main.clearPosts();
		postList.clear();
		lastDisplayedID = -1;
	}

	// Fetches posts based on the sort and filter options and adds them to postList
	// They are fetched from a database file
	public static void fetchPosts() {
		ResultSet result;
		if(sortByVotes) {
			//result = sqLiteHandler.getPostsByVote(Constants.POST_GRAB_SIZE, postList.size());
			result = DataAccesser.fetchPostsByVote();
		} else {
			result = DataAccesser.fetchPostsByDate(Constants.POST_GRAB_SIZE, postList.size());
		}
		if(result == null) {
			System.out.println("Error fetching posts");
			return;
		}

		//Try to add posts to postList
		int id, accountID;
		String title, textContent, timeStamp, embedLink;
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
						embedLink = result.getString("embedLink");
						textContent = result.getString("textContent");
						timeStamp = result.getString("timeStamp");
						//Create a new post and add it to the list
						Post post = new Post(id, accountID, title, embedLink, textContent, timeStamp);

						postList.add(post);

					}
				} catch (Exception ex) {
					System.out.println("Failed to parse result: " + ex.getMessage());
				}
			}
		} catch(Exception ex) {
			System.out.println("resultset was not valid: " + ex.getMessage());
		}
	}
}