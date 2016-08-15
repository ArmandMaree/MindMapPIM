package data;

import java.io.Serializable;

public class EditSettingsResponse implements Serializable {
	//private static final long serialVersionUID = 3312696172406791L;

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
	public EditSettingsResponse(Boolean success, String requestFeedback) {
		super();
		this.success = success;
		this.requestFeedback = requestFeedback;
	}

	/**
	* Create string representation of EditSettingsResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		return "EditSettingsResponse {\n" +
			"\tsuccess: [\n" + success + "\n" +
			"\trequestFeedback: [\n" + requestFeedback + "\n" +
		"}";
	}
}
