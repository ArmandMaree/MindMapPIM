package poller;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* The information of a user as used by a PIM {@link poller.TwitterPoller}.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class TwitterPollingUser implements Serializable {
	private static final long serialVersionUID = 2867444354698740L;

	/**
	* Id of the user as used by the database.
	*/
	@Id
	private String id;

	/**
	* Id of the user as used by the PIM.
	*/
	private String userId = null;

	/**
	* The ID of the post that indicates the start of the block of tweets that is currently being processed.
	*/
	private long startOfBlockTweetId = 1;

	/**
	* The ID of the post that indicates the current post in the block of tweets that is currently being processed.
	*/
	private long currentTweetId = 1;

	/**
	* The ID of the post that indicates the end of the block of tweets that is currently being processed.
	*/
	private long endOfBlockTweetId = 1;

	/**
	* Indicates whether this user has a poller running on this account.
	*/
	private boolean currentlyPolling = false;

	/**
	* Number of tweets processed for this user.
	*/
	private int numberOfTweets = 0;

	/**
	* Default constructor
	*/
	public TwitterPollingUser() {
		super();
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param userId Id of the user as used by the PIM.
	* @param accessToken The access token that can be used to generate new access tokens.
	*/
	public TwitterPollingUser(String userId) {
		super();
		this.userId = userId;
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param id Id of the user as used by the database.
	* @param userId Id of the user as used by the PIM.
	* @param accessToken The access token that can be used to generate new access tokens.
	*/
	public TwitterPollingUser(String id, String userId) {
		super();
		this.id = id;
		this.userId = userId;
	}

	/**
	* Returns value of id
	* @return ID used in the repository.
	*/
	public String getId() {
		return id;
	}

	/**
	* Sets new value of id
	* @param id ID used in the repository.
	*/
	public void setId(String id) {
		this.id = id;
	}

	/**
	* Returns value of userId
	* @return ID of the user used by the poller.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Sets new value of userId
	* @param userId ID of the user used by the poller.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns value of startOfBlockTweetId
	* @return The ID of the post that indicates the start of the block of tweets that is currently being processed.
	*/
	public long getStartOfBlockTweetId() {
		return startOfBlockTweetId;
	}

	/**
	* Sets new value of startOfBlockTweetId
	* @param startOfBlockTweetId The ID of the post that indicates the start of the block of tweets that is currently being processed.
	*/
	public void setStartOfBlockTweetId(long startOfBlockTweetId) {
		this.startOfBlockTweetId = startOfBlockTweetId;
	}

	/**
	* Returns value of currentTweetId
	* @return The ID of the post that indicates the current post in the block of tweets that is currently being processed.
	*/
	public long getCurrentTweetId() {
		return currentTweetId;
	}

	/**
	* Sets new value of currentTweetId
	* @param currentTweetId The ID of the post that indicates the current post in the block of tweets that is currently being processed.
	*/
	public void setCurrentTweetId(long currentTweetId) {
		this.currentTweetId = currentTweetId;
	}

	/**
	* Returns value of endOfBlockTweetId
	* @return The ID of the post that indicates the end of the block of tweets that is currently being processed.
	*/
	public long getEndOfBlockTweetId() {
		return endOfBlockTweetId;
	}

	/**
	* Sets new value of endOfBlockTweetId
	* @param endOfBlockTweetId The ID of the post that indicates the end of the block of tweets that is currently being processed.
	*/
	public void setEndOfBlockTweetId(long endOfBlockTweetId) {
		this.endOfBlockTweetId = endOfBlockTweetId;
	}

	/**
	* Returns value of currentlyPolling
	* @return Indicates whether this user has a poller running on this account.
	*/
	public boolean getCurrentlyPolling() {
		return currentlyPolling;
	}

	/**
	* Sets new value of currentlyPolling
	* @param currentlyPolling Indicates whether this user has a poller running on this account.
	*/
	public void setCurrentlyPolling(boolean currentlyPolling) {
		this.currentlyPolling = currentlyPolling;
	}

	/**
	* Returns value of numberOfTweets
	* @return Number of tweets processed for this user.
	*/
	public int getNumberOfTweets() {
		return numberOfTweets;
	}

	/**
	* Sets new value of numberOfTweets
	* @param numberOfTweets Number of tweets processed for this user.
	*/
	public void setNumberOfTweets(int numberOfTweets) {
		this.numberOfTweets = numberOfTweets;
	}

	/**
	* Increment the value of numberOfTweets
	* @param numberOfTweets Number of tweets processed for this user.
	*/
	public void incrementNumberOfTweets() {
		numberOfTweets++;
	}

	/**
	* A thread safe way to determine whether the user has a poller running for them.
	* @return False if no poller is running (this will automatically change the state to currently polling) other wise true.
	*/
	public synchronized boolean checkAndStart() {
		if (currentlyPolling)
			return true;
		else {
			currentlyPolling = true;
			return false;
		}
	}

	/**
	* Create string representation of PollingUser for printing.
	* @return String represntation of a PollingUser.
	*/
	@Override
	public String toString() {
		return "PollingUser {\n" +
			"\tid: " + id + ",\n" +
			"\tuserId: " + userId + ",\n" +
			"\tstartOfBlockTweetId: " + startOfBlockTweetId + ",\n" +
			"\tcurrentTweetId: " + currentTweetId + ",\n" +
			"\tendOfBlockTweetId: " + endOfBlockTweetId + ",\n" +
			"\tcurrentlyPolling: " + currentlyPolling + "\n" +
		"}";
	}
}
