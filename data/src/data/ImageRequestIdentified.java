package data;

import java.io.Serializable;
import java.util.List;

/**
* This class is a wrapper for an ImageRequest by adding a return ID to it.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ImageRequestIdentified extends ImageRequest implements Serializable {
	private static final long serialVersionUID = 7295802455448705L;

	private String returnId;

	/**
	* Default constructor.
	*/
	public ImageRequestIdentified() {
		super();
		returnId = null;
	}	

	/**
	* Constructor if no source is required.
	* @param returnId A unique ID that identified this request. It will be included in the response.
	* @param topics The list of topics for which images are required.
	*/
	public ImageRequestIdentified(String returnId, List<String> topics) {
		super(topics);
		this.returnId = returnId;
	}

	/**
	* Constructor if a source is required.
	* @param returnId A unique ID that identified this request. It will be included in the response.
	* @param topics The list of topics for which images are required.
	* @param source The source where images must be retrieved from (like google or bing). Null if any source is fine. 
	*/
	public ImageRequestIdentified(String returnId, List<String> topics, String source) {
		super(topics, source);
		this.returnId = returnId;
	}

	/**
	* Set the value of returnId.
	* @param returnId A unique ID that identified this request. It will be included in the response.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Get the value of returnId.
	* @return A unique ID that identified this request. It will be included in the response.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Get a string representation of this ImageRequestIdentified object used for printing.
	* @return A string reprentation of this object.
	*/
	@Override
	public String toString() {
		String t = "";

		for (String tS : getTopics()) {
			t += "\t\t" + tS + "\n";
		}

		return "ImageRequestIdentified {\n" +
			"\treturnId: " + returnId + "\n" +
			"\tsource: " + getSource() + "\n" +
			"\ttopics: [\n" + t + "\t]\n" +
		"}";
	}
}