package data;

import java.io.Serializable;

/**
* Used by the RESTController to map JSON objects to. Contains all the information needed to create a User object to be persisted.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class UserRegistration implements Serializable {
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
	* Default empty constructor.
	*/
	public UserRegistration() {

	}

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
	* Set the value of firstName.
	* @param firstName First name of the user.
	*/
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	* Returns the value of lastName.
	* @return Last name of the user.
	*/
	public String getLastName() {
		return lastName;
	}

	/**
	* Set the value of lastName.
	* @param lastName The last name of the user.
	*/
	public void setLastName(String lastName) {
		this.lastName = lastName;
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
	* String representation of UserRegistration used for printing.
	* @return UserRegistration in string form.
	*/
	@Override
	public String toString() {
		return "UserRegistration {\n" +
			"\tfirstName: " + firstName + "\n" +
			"\tlastName: " + lastName + "\n" +
			"\tauthCodes size: " + ((authCodes == null) ? "null" : authCodes.length) + "\n" +
		"}";
	}
}
