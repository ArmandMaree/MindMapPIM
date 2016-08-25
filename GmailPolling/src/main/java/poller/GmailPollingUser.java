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
	private String userId;

	/**
	* The refresh token that can be used to generate new access tokens.
	*/
	private String refreshToken;

	/**
	* The email with the smallest date that was proccessed.
	*/
	private String earliestEmail;

	/**
	* The email with the largest date that was processed.
	*/
	private String lastEmail;

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
	* Returns value of earliestEmail
	* @return The id of the most recent item.
	*/
	public String getEarliestEmail() {
		return earliestEmail;
	}

	/**
	* Sets new value of earliestEmail
	* @param earliestEmail The id of the most recent item.
	*/
	public void setEarliestEmail(String earliestEmail) {
		this.earliestEmail = earliestEmail;
	}

	/**
	* Returns value of lastEmail
	* @return The date of the latest item.
	*/
	public String getLastEmail() {
		return earliestEmail;
	}

	/**
	* Sets new value of lastEmail
	* @param lastEmail The date of the latest item.
	*/
	public void setLastEmail(String lastEmail) {
		this.lastEmail = lastEmail;
	}

	/**
	* Create string representation of PollingUser for printing.
	* @return String represntation of a PollingUser.
	*/
	@Override
	public String toString() {
		return "PollingUser {\n" +
			"\tid: " + id + ",\n" +
			"userId: " + userId + ",\n" +
			"refreshToken: " + refreshToken + "\n" +
		"}";
	}
}
