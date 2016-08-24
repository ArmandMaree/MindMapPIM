package data;

import java.io.Serializable;

public class UserUpdateRequest extends User implements Serializable {
	private static final long serialVersionUID = 369603720519056L;

	/**
	* Array of authentication codes for the pollers.
	*/
	private AuthCode[] authCodes = null;

	/**
	* Default empty conctructor.
	*/
	public UserUpdateRequest() {
		super();
	}

	/**
	* Default constructor used when updating.
	*/
	public UserUpdateRequest(String userId, AuthCode[] authCodes) {
		super(userId);
		this.authCodes = authCodes;
	}

	/**
	* Returns the value of authCodes.
	* @return Array of authentication codes for the pollers.
	*/
	public AuthCode[] getAuthCodes() {
		return authCodes;
	}

	/**
	* Set the value of authCodes.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public void setAuthCodes(AuthCode[] authCodes) {
		this.authCodes = authCodes;
	}

	public String toString() {
		return "UserUpdateRequest {\n" +
			"\tid: " + getUserId() + ",\n" +
			"\tfirstName: " + getFirstName()  + ",\n" +
			"\tlastName: " + getLastName() + ",\n" +
			"\tgmailId: " + getGmailId() + "\n" +
			"\tauthCodes size: " + ((authCodes == null) ? "null" : authCodes.length) + "\n" +
		"}";
	}
}