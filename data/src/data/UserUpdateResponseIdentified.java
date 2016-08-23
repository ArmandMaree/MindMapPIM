package data;

import java.io.Serializable;

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

	public UserUpdateResponseIdentified(String returnId, int code) {
		super(code);
		this.returnId = returnId;
	}

	public UserUpdateResponseIdentified(String returnId) {
		super();
		this.returnId = returnId;
	}

	/**
	* Returns the value of returnId.
	* @return 
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Set the value of returnId.
	* @param returnId
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	@Override
	public String toString() {
		return "UserUpdateResponseIdentified {\n" +
			"\treturnId: [\n" + returnId + "\n" +
			"\tcode: " + getCode() + "\n" +
		"}";
	}
}