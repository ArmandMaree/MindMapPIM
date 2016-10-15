package data;

import java.io.Serializable;
import java.util.List;

/**
* This class is a wrapper for an ImageResponse by adding a return ID to it.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ImageResponseIdentified extends ImageResponse implements Serializable {
	private static final long serialVersionUID = 4428195657980846L;

	private String returnId;

	/**
	* Default constructor.
	*/
	public ImageResponseIdentified() {
		super();
		returnId = null;
	}	

	/**
	* Constructor that initializes with default values.
	* @param returnId A unique ID that identified this request. It will be included in the response.
	*/
	public ImageResponseIdentified(String returnId) {
		super();
		this.returnId = returnId;
	}

	/**
	* Constructor if no source is required.
	* @param returnId A unique ID that identified this request. It will be included in the response.
	* @param topics The list of topics for which images are required.
	*/
	public ImageResponseIdentified(String returnId, List<ImageDetails> topics) {
		super(topics);
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
	* Get a string representation of this ImageResponseIdentified object used for printing.
	* @return A string reprentation of this object.
	*/
	@Override
	public String toString() {
		String t = "";

		if (getImageDetails() != null)
			for (ImageDetails imageDetails : getImageDetails()) {
				t += "\t\t" + imageDetails.getTopic() + " (" + imageDetails.getSource() + "): " + imageDetails.getUrl() + "\n";
			}

		return "ImageResponseIdentified {\n" +
			"\treturnId: " + returnId + "\n" +
			"\ttopics: [\n" + t + "\t]\n" +
		"}";
	}
}