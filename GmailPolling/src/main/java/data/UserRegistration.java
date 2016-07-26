package data;

/**
* Used by the RESTController to map JSON objects to. Contains all the information needed to create a User object to be persisted.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class UserRegistration {
	/*
	* First name of the user.
	*/
	private String firstName;

	/*
	* Last name of the user.
	*/
	private String lastName;

	/**
	* Array of authentication codes for the pollers.
	*/
	private AuthCode[] authCodes;

	/**
	* Default constructor.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public UserRegistration(String firstName, String lastName, AuthCode[] authCodes) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.authCodes = authCodes;
	}

	/**
	* Returns the value of firstName.
	* @return First name of the user.
	*/
	public String getFirstName() {
		return firstName;
	}

	/**
	* Returns the value of lastName.
	* @return Last name of the user.
	*/
	public String getLastName() {
		return lastName;
	}

	/**
	* Returns the value of authCodes.
	* @return Array of authentication codes for the pollers.
	*/
	public AuthCode[] getAuthCodes() {
		return authCodes;
	}
}