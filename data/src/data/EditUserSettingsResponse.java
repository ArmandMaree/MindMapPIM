package data;

import java.io.Serializable;

public class EditUserSettingsResponse implements Serializable {
	private static final long serialVersionUID = 3312696172406796L;
	String returnId;
	boolean success;
	/**
	* Default empty EditUserSettingsResponse constructor
	*/
	public EditUserSettingsResponse() {
		super();
	}

	/**
	* Default EditUserSettingsResponse constructor
	*/
	public EditUserSettingsResponse(String returnId, boolean success) {
		super();
		this.returnId = returnId;
		this.success = success;
	}
	public String getReturnId()
	{
		return returnId;
	}

	/**
	* Create string representation of EditUserSettingsResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		return "EditUserSettingsResponse {\n" +
			"\treturnId: [\n" + returnId + "\n" +
			"\tsuccess: [\n" + success + "\n" +
		"}";
	}
}
