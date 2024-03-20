import java.util.*;

public class PostManager {
	private List<Post> postList;
	private boolean sortByVotes; // If False, defaults to sorting by new.
	private boolean filterByFollowed;
	private boolean filterByTime;
	private int timeFilterDays;

	// By default, sortByVotes is false, filterByTime is true, and timeFilterDays is 7.
	// Sets postList by calling fetchPosts()
	public PostManager(){

	}

	// Fetches posts based on the sort and filter options and adds them to postList
	// They are fetched from a data file
	public void fetchPosts() {

	}

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
}