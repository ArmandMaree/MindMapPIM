package data;

import java.io.Serializable;

public class UserUpdateRequestIdentified extends UserUpdateRequest implements Serializable {
	private static final long serialVersionUID = 4129067728810262L;
	
	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Default empty constructor.
	*/
	public UserUpdateRequestIdentified() {
		super();
	}

	/**
	* Default constructor used when updating a user.
	*/
	public UserUpdateRequestIdentified(String returnId, String userId, AuthCode[] authCodes) {
		super(userId, authCodes);
		this.returnId = returnId;
	}

	/**
	* Returns value of returnId
	* @return ID used to identify the original request.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Sets new value of id
	* @param returnId ID used to identify the original request.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public String toString() {
		return "UserUpdateRequestIdentified {\n" +
			"\treturn: " + returnId + ",\n" +
			"\tid: " + getUserId() + ",\n" +
			"\tfirstName: " + getFirstName()  + ",\n" +
			"\tlastName: " + getLastName() + ",\n" +
			"\tgmailId: " + getGmailId() + "\n" +
			"\tauthCodes size: " + ((getAuthCodes() == null) ? "null" : getAuthCodes().length) + "\n" +
		"}";
	}
}