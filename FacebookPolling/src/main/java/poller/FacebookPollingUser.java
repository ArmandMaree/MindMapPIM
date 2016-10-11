package poller;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* The information of a user as used by a PIM {@link poller.Poller}.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FacebookPollingUser implements Serializable {
	private static final long serialVersionUID = 1882366893756446L;

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
	* The access token that can be used to generate new access tokens.
	*/
	private String accessToken = null;

	/**
	* The time the accessToken expires.
	*/
	private long expireTime = -1;

	/**
	* The ID of the post that indicates the start of the block of posts that is currently being processed.
	*/
	private String startOfBlockPostId = null;

	/**
	* The ID of the post that indicates the current post in the block of posts that is currently being processed.
	*/
	private String currentPostId = null;

	/**
	* The ID of the post that indicates the end of the block of posts that is currently being processed.
	*/
	private String endOfBlockPostId = null;

	/**
	* Indicates whether this user has a poller running on this account.
	*/
	private boolean currentlyPolling = false;

	/**
	* Number of posts processed for this user.
	*/
	private int numberOfPosts = 0;

	/**
	* Default constructor
	*/
	public FacebookPollingUser() {
		super();
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param userId Id of the user as used by the PIM.
	* @param accessToken The access token that can be used to generate new access tokens.
	*/
	public FacebookPollingUser(String userId, String accessToken, long expireTime) {
		super();
		this.userId = userId;
		this.accessToken = accessToken;
		this.expireTime = expireTime;
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param id Id of the user as used by the database.
	* @param userId Id of the user as used by the PIM.
	* @param accessToken The access token that can be used to generate new access tokens.
	*/
	public FacebookPollingUser(String id, String userId, String accessToken) {
		super();
		this.id = id;
		this.userId = userId;
		this.accessToken = accessToken;
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
	* Returns value of accessToken
	* @return Token used to get a new access token.
	*/
	public String getAccessToken() {
		return accessToken;
	}

	/**
	* Sets new value of accessToken
	* @param accessToken Token used to get a new access token.
	*/
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	* Returns value of expireTime
	* @return The time the accessToken expires.
	*/
	public long getExpireTime() {
		return expireTime;
	}

	/**
	* Sets new value of expireTime
	* @param expireTime The time the accessToken expires.
	*/
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	/**
	* Returns value of startOfBlockPostId
	* @return The ID of the post that indicates the start of the block of posts that is currently being processed.
	*/
	public String getStartOfBlockPostId() {
		return startOfBlockPostId;
	}

	/**
	* Sets new value of startOfBlockPostId
	* @param startOfBlockPostId The ID of the post that indicates the start of the block of posts that is currently being processed.
	*/
	public void setStartOfBlockPostId(String startOfBlockPostId) {
		this.startOfBlockPostId = startOfBlockPostId;
	}

	/**
	* Returns value of currentPostId
	* @return The ID of the post that indicates the current post in the block of posts that is currently being processed.
	*/
	public String getCurrentPostId() {
		return currentPostId;
	}

	/**
	* Sets new value of currentPostId
	* @param currentPostId The ID of the post that indicates the current post in the block of posts that is currently being processed.
	*/
	public void setCurrentPostId(String currentPostId) {
		this.currentPostId = currentPostId;
	}

	/**
	* Returns value of endOfBlockPostId
	* @return The ID of the post that indicates the end of the block of posts that is currently being processed.
	*/
	public String getEndOfBlockPostId() {
		return endOfBlockPostId;
	}

	/**
	* Sets new value of endOfBlockPostId
	* @param endOfBlockPostId The ID of the post that indicates the end of the block of posts that is currently being processed.
	*/
	public void setEndOfBlockPostId(String endOfBlockPostId) {
		this.endOfBlockPostId = endOfBlockPostId;
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
	* Returns value of numberOfPosts
	* @return Number of posts processed for this user.
	*/
	public int getNumberOfPosts() {
		return numberOfPosts;
	}

	/**
	* Sets new value of numberOfPosts
	* @param numberOfPosts Number of posts processed for this user.
	*/
	public void setNumberOfPosts(int numberOfPosts) {
		this.numberOfPosts = numberOfPosts;
	}

	/**
	* Increment the value of numberOfPosts
	* @param numberOfPosts Number of posts processed for this user.
	*/
	public void incrementNumberOfPosts() {
		numberOfPosts++;
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
		return "FacebookPollingUser {\n" +
			"\tid: " + id + ",\n" +
			"\tuserId: " + userId + ",\n" +
			"\tstartOfBlockPostId: " + startOfBlockPostId + ",\n" +
			"\tcurrentPostId: " + currentPostId + ",\n" +
			"\tendOfBlockPostId: " + endOfBlockPostId + ",\n" +
			"\tcurrentlyPolling: " + currentlyPolling + "\n" +
		"}";
	}
}
