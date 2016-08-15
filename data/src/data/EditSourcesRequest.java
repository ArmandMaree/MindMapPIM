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
	private String returnId;
	private User user;
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
	* @param returnId The id of the request
	* @param user the id of the user the request is for.
	* @param gmailChanged A boolean value indicating if the respective source has been changed
	* @param gmailId The gmail user id obtained through Google API
	* @param gmailAccessToken The access token supplied by Gmail
	* @param facebookChanged A boolean value indicating if the respective source has been changed
	* @param facebookId The gmail user id obtained through Google API
	* @param facebookAccessToken The access token supplied by Gmail
	*/
	public EditSourcesRequest(String returnId,User user, Boolean gmailChanged, String gmailId, String gmailAccessToken, Boolean facebookChanged, String facebookId, String facebookAccessToken ) {
		this.returnId = returnId;
		this.user = user;
		this.gmailChanged = gmailChanged;
		this.gmailId = gmailId;
		this.gmailAccessToken = gmailAccessToken;
		this.facebookChanged = facebookChanged;
		this.facebookId = facebookId;
		this.facebookAccessToken = facebookAccessToken;
	}

	/**
	* Get the value of returnId.
	* @return The id of the request that has been sent.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Set the value of returnId
	* @param returnId The id of the request that has been sent.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Get the value of user.
	* @return The id of the user the request is for.
	*/
	public String getUser() {
		return user;
	}

	/**
	* Set the value of user
	* @param user The id of the user the request is for.
	*/
	public void setUser(String user) {
		this.user = user;
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
			"\tuser: " + user + "\n" +
			"\tgmailChanged: " + gmailChanged + "\n" +
			"\tgmailId: " + gmailId + "\n" +
			"\tgmailAccessToken: " + gmailAccessToken + "\n" +
			"\tfacebookChanged: " + facebookChanged + "\n" +
			"\tfacebookId: " + facebookId + "\n" +
			"\tfacebookAccessToken: " + facebookAccessToken + "\n" +

			"}";
	}
}