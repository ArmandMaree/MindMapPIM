package data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class PollingUser implements Serializable {
	@Id
	private String id;
	private String userId;
	private String refreshToken;

	/**
	* Default empty PollingUser constructor
	*/
	public PollingUser() {
		super();
	}

	/**
	* Default PollingUser constructor
	*/
	public PollingUser(String userId, String refreshToken) {
		super();
		this.userId = userId;
		this.refreshToken = refreshToken;
	}

	/**
	* Default PollingUser constructor
	*/
	public PollingUser(String id, String userId, String refreshToken) {
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
	* Create string representation of PollingUser for printing
	* @return
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
