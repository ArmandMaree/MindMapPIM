package data;

import java.io.Serializable;

public class EditSettingsResponse implements Serializable {
	//private static final long serialVersionUID = 3312696172406791L;
	String returnId;
	String requestFeedback;
	Boolean success;
	/**
	* Default empty EditSettingsResponse constructor
	*/
	public EditSettingsResponse() {
		super();
	}

	/**
	* Default EditSettingsResponse constructor
	*/
	public EditSettingsResponse(String returnId,String requestFeedback,Boolean success) {
		super();
		this.returnId = returnId;
		this.requestFeedback = requestFeedback;
		this.success = success;
	}

	/**
	* Create string representation of EditSettingsResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		return "EditSettingsResponse {\n" +
			"\treturnId: [\n" + returnId + "\n" +
			"\tsuccess: [\n" + success + "\n" +
			"\trequestFeedback: [\n" + requestFeedback + "\n" +
		"}";
	}
}
