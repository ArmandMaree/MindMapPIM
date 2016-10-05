package poller;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* The information of a user as used by a PIM {@link poller.Poller}.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class GmailPollingUser implements Serializable {
	private static final long serialVersionUID = 3213026260976085L;

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
	* The refresh token that can be used to generate new access tokens.
	*/
	private String refreshToken = null;

	/**
	* The ID of the email that indicates the start of the block of emails that is currently being processed.
	*/
	private String startOfBlockEmailId = null;

	/**
	* The ID of the email that indicates the current email in the block of emails that is currently being processed.
	*/
	private String currentEmailId = null;

	/**
	* The ID of the email that indicates the end of the block of emails that is currently being processed.
	*/
	private String endOfBlockEmailId = null;

	/**
	* Indicates whether this user has a poller running on this account.
	*/
	private boolean currentlyPolling = false;

	/**
	* Number of emails processed for this user.
	*/
	private int numberOfEmails = 0;

	/**
	* Default constructor
	*/
	public GmailPollingUser() {
		super();
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param userId Id of the user as used by the PIM.
	* @param refreshToken The refresh token that can be used to generate new access tokens.
	*/
	public GmailPollingUser(String userId, String refreshToken) {
		super();
		this.userId = userId;
		this.refreshToken = refreshToken;
	}

	/**
	* Constructor that initializes some mamber variables.
	* @param id Id of the user as used by the database.
	* @param userId Id of the user as used by the PIM.
	* @param refreshToken The refresh token that can be used to generate new access tokens.
	*/
	public GmailPollingUser(String id, String userId, String refreshToken) {
		super();
		this.id = id;
		this.userId = userId;
		this.refreshToken = refreshToken;
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
	* Returns value of refreshToken
	* @return Token used to get a new access token.
	*/
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	* Sets new value of refreshToken
	* @param refreshToken Token used to get a new access token.
	*/
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	* Returns value of startOfBlockEmailId
	* @return The ID of the email that indicates the start of the block of emails that is currently being processed.
	*/
	public String getStartOfBlockEmailId() {
		return startOfBlockEmailId;
	}

	/**
	* Sets new value of startOfBlockEmailId
	* @param startOfBlockEmailId The ID of the email that indicates the start of the block of emails that is currently being processed.
	*/
	public void setStartOfBlockEmailId(String startOfBlockEmailId) {
		this.startOfBlockEmailId = startOfBlockEmailId;
	}

	/**
	* Returns value of currentEmailId
	* @return The ID of the email that indicates the current email in the block of emails that is currently being processed.
	*/
	public String getCurrentEmailId() {
		return currentEmailId;
	}

	/**
	* Sets new value of currentEmailId
	* @param currentEmailId The ID of the email that indicates the current email in the block of emails that is currently being processed.
	*/
	public void setCurrentEmailId(String currentEmailId) {
		this.currentEmailId = currentEmailId;
	}

	/**
	* Returns value of endOfBlockEmailId
	* @return The ID of the email that indicates the end of the block of emails that is currently being processed.
	*/
	public String getEndOfBlockEmailId() {
		return endOfBlockEmailId;
	}

	/**
	* Sets new value of endOfBlockEmailId
	* @param endOfBlockEmailId The ID of the email that indicates the end of the block of emails that is currently being processed.
	*/
	public void setEndOfBlockEmailId(String endOfBlockEmailId) {
		this.endOfBlockEmailId = endOfBlockEmailId;
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
	* Returns value of numberOfEmails
	* @return Number of emails processed for this user.
	*/
	public int getNumberOfEmails() {
		return numberOfEmails;
	}

	/**
	* Sets new value of numberOfEmails
	* @param numberOfEmails Number of emails processed for this user.
	*/
	public void setNumberOfEmails(int numberOfEmails) {
		this.numberOfEmails = numberOfEmails;
	}

	/**
	* Increment the value of numberOfEmails
	* @param numberOfEmails Number of emails processed for this user.
	*/
	public void incrementNumberOfEmails() {
		numberOfEmails++;
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
			"\tstartOfBlockEmailId: " + startOfBlockEmailId + ",\n" +
			"\tcurrentEmailId: " + currentEmailId + ",\n" +
			"\tendOfBlockEmailId: " + endOfBlockEmailId + ",\n" +
			"\tcurrentlyPolling: " + currentlyPolling + "\n" +
		"}";
	}
}
