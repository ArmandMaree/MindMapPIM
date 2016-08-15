package data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* A request to store details of added/removed data sources.
*
* @author  Amy Lochner
* @since   2016-08-15
*/
public class EditSourcesRequest implements Serializable {
	
	// private static final long serialVersionUID = ???;
	private String userId;
	private Boolean gmailChanged;
	private String gmailId;
	private String gmailAccessToken;
	private Boolean facebookChanged;
	private String facebookId;
	private String facebookAccessToken;

	public EditSourcesRequest(){

	}
	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param gmailChanged A boolean value indicating if the respective source has been changed
	* @param gmailId The gmail user id obtained through Google API
	* @param gmailAccessToken The access token supplied by Gmail
	* @param facebookChanged A boolean value indicating if the respective source has been changed
	* @param facebookId The gmail user id obtained through Google API
	* @param facebookAccessToken The access token supplied by Gmail
	*/
	public TopicRequest(String userId, Boolean gmailChanged, String gmailId, String gmailAccessToken, Boolean facebookChanged, String facebookId, String facebookAccessToken ) {
		this.userId = userId;
		this.gmailChanged = gmailChanged;
		this.gmailId = gmailId;
		this.gmailAccessToken = gmailAccessToken;
		this.facebookChanged = facebookChanged;
		this.facebookId = facebookId;
		this.facebookAccessToken = facebookAccessToken;
	}

	/**
	* Get the value of userId.
	* @return The id of the user the request is for.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Set the value of userId
	* @param userId The id of the user the request is for.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Get the value gmailChanged
	* @return The value of gmailChanged i.e True/False
	*/
	public Boolean getGmailChanged() {
		return gmailChanged;
	}

	/**
	* Set the value gmailChanged
	* @param The value that gmailChanged is set to i.e True/False
	*/
	public void setGmailChanged(Boolean gmailChanged) {
		this.gmailChanged = gmailChanged;
	}
	/**
	* Get the value gmailId
	* @return The value of gmailID 
	*/
	public Boolean getGmailId() {
		return gmailId;
	}

	/**
	* Set the value gmailId
	* @param The value that gmailId is set to 
	*/
	public void setGmailId(Boolean gmailChanged) {
		this.gmailChanged = gmailChanged;
	}
	/**
	* Get the value gmailAccessToken
	* @return The value of gmailAccessToken 
	*/
	public Boolean getGmailAccessToken() {
		return gmailAccessToken;
	}

	/**
	* Set the value gmailAccessToken
	* @param The value that gmailAccessToken is set to 
	*/
	public void setGmailAccessToken(String gmailAccessToken) {
		this.gmailAccessToken = gmailAccessToken;
	}

	/**
	* Get the value gmailChanged
	* @return The value of gmailChanged i.e True/False
	*/
	public Boolean getFacebookChanged() {
		return facebookChanged;
	}

	/**
	* Set the value gmailChanged
	* @param The value that gmailChanged is set to i.e True/False
	*/
	public void setFacebookChanged(Boolean facebookChanged) {
		this.facebookChanged = facebookChanged;
	}
	/**
	* Get the value gmailId
	* @return The value of gmailID 
	*/
	public Boolean getFacebookId() {
		return facebookId;
	}

	/**
	* Set the value gmailId
	* @param The value that gmailId is set to 
	*/
	public void setFacebookId(String facebookId) {
		this.faceboodId = facebookId;
	}
	/**
	* Get the value gmailAccessToken
	* @return The value of gmailAccessToken 
	*/
	public Boolean getFacebookAccessToken() {
		return facebookAccessToken;
	}

	/**
	* Set the value gmailAccessToken
	* @param The value that gmailAccessToken is set to 
	*/
	public void setFacebookAccessToken(String facebookAccessToken) {
		this.facebookAccessToken = facebookAccessToken;
	}

	
	/**
	* Create string representation of TopicRequest for printing
	* @return TopicRequest as a string.
	*/
	@Override
	public String toString() {

		return "EditSourcesRequest{\n" +
			"\tuserId: " + userId + "\n" +
			"\tgmailChanged: " + gmailChanged + "\n" +
			"\tgmailId: " + gmailId + "\n" +
			"\tgmailAccessToken: " + gmailAccessToken + "\n" +
			"\tfacebookChanged: " + facebookChanged + "\n" +
			"\tfacebookId: " + facebookId + "\n" +
			"\tfacebookAccessToken: " + facebookAccessToken + "\n" +

			"}";
	}
}