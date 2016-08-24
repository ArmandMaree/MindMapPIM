package data;

import java.io.Serializable;

/**
* Contains all the information needed to update a User object that was previously persisted.
*
* @author  Armand Maree
* @since   1.0.0
*/
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
	* Constructor that initializes some member variables used for updating.
	* @param userId ID used in database.
	* @param authCodes Array of authentication codes for the pollers.
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

	/**
	* String representation of a UserUpdateRequest used for printing.
	* @return String representation of a UserUpdateRequest.
	*/
	@Override
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