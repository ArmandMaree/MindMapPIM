package data;

import java.io.Serializable;

/**
* A response to a previous request to update a user.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserUpdateResponse implements Serializable {
	/**
	* Update was done successfully.
	*/
	public static final int SUCCESS = 0;

	/**
	* A user could not be found that has matching details.
	*/
	public static final int USER_NOT_FOUND = 1;

	/**
	* Some unknown error occurred.
	*/
	public static final int UNKNOWN = 99;

	private static final long serialVersionUID = 2749028836853267L;

	/**
	* Code to indicate the success status of the request processing.
	*/
	int code = UNKNOWN;

	/**
	* Default empty UserUpdateResponse constructor
	*/
	public UserUpdateResponse() {
		super();
	}

	/**
	* Constructor that initializes some member variables.
	* @param code Code to indicate the success status of the request processing.
	*/
	public UserUpdateResponse(int code) {
		super();
		this.code = code;
	}

	/**
	* Return the value of code.
	* @return Code to indicate the success status of the request processing.
	*/
	public int getCode() {
		return code;
	}

	/**
	* Set the value of code.
	* @param code Code to indicate the success status of the request processing.
	*/
	public void setCode(int code) {
		this.code = code;
	}

	/**
	* Create string representation of UserUpdateResponse for printing
	* @return String representation of UserUpdateResponse.
	*/
	@Override
	public String toString() {
		return "UserUpdateResponse {\n" +
			"\tcode: " + code + "\n" +
		"}";
	}
}
