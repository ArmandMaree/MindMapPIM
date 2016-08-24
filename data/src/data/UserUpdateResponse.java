package data;

import java.io.Serializable;

public class UserUpdateResponse implements Serializable {
	public static final int SUCCESS = 0;
	public static final int USER_NOT_FOUND = 1;
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
	* Default UserUpdateResponse constructor
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
	* @return
	*/
	@Override
	public String toString() {
		return "UserUpdateResponse {\n" +
			"\tcode: " + code + "\n" +
		"}";
	}
}
