package data;

import java.io.Serializable;

public class EditSourcesResponse implements Serializable {
	//private static final long serialVersionUID = 3312696172406791L;

	String requestFeedback;
	Boolean success;
	/**
	* Default empty EditSourcesResponse constructor
	*/
	public EditSourcesResponse() {
		super();
	}

	/**
	* Default EditSourcesResponse constructor
	*/
	public EditSourcesResponse(Boolean success, String requestFeedback) {
		super();
		this.success = success;
		this.requestFeedback = requestFeedback;
	}

	/**
	* Create string representation of EditSourcesResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		return "EditSourcesResponse {\n" +
			"\tsuccess: [\n" + success + "\n" +
			"\trequestFeedback: [\n" + requestFeedback + "\n" +
		"}";
	}
}
