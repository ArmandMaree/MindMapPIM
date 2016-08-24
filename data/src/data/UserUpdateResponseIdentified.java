package data;

import java.io.Serializable;

/**
* A response to a previous request to update a user with an attached returnId.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserUpdateResponseIdentified extends UserUpdateResponse implements Serializable {
	/**
	* The ID of the response. This should match the ID of the request.
	*/
	private String returnId;

	/**
	* Default empty constructor.
	*/
	public UserUpdateResponseIdentified() {
		super();
	}

	/**
	* Constructor that initializes some member variables.
	* @param returnId The ID of the response. This should match the ID of the request.
	* @param code Code to indicate the success status of the request processing.
	*/
	public UserUpdateResponseIdentified(String returnId, int code) {
		super(code);
		this.returnId = returnId;
	}

	/**
	* Constructor that initializes some member variables.
	* @param returnId The ID of the response. This should match the ID of the request.
	*/
	public UserUpdateResponseIdentified(String returnId) {
		super();
		this.returnId = returnId;
	}

	/**
	* Returns the value of returnId.
	* @return The ID of the response. This should match the ID of the request.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Set the value of returnId.
	* @param returnId The ID of the response. This should match the ID of the request.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Create string representation of UserUpdateResponseIdentified for printing
	* @return String representation of UserUpdateResponseIdentified.
	*/
	@Override
	public String toString() {
		return "UserUpdateResponseIdentified {\n" +
			"\treturnId: \n" + returnId + "\n" +
			"\tcode: " + getCode() + "\n" +
		"}";
	}
}