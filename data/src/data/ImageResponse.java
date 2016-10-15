package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
* This class contains all the images based on some previous {@link data.ImageRequest}.
*
* <p>
*	This class will contain all the {@link data.ImageDetails}' details that were given in a request. If a topic's image could not be found it will still be in this response but the {@link data.ImageDetails#url} and {@link data.ImageDetails#source} will be null.
* </p>
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ImageResponse implements Serializable {
	private static final long serialVersionUID = 1156251884479771L;

	private List<ImageDetails> imageDetails;

	/**
	* Default constructor.
	*/
	public ImageResponse() {
		imageDetails = new ArrayList<>();
	}

	/**
	* Constructor.
	* @param imageDetails The images that has to be saved.
	*/
	public ImageResponse(List<ImageDetails> imageDetails) {
		this.imageDetails = imageDetails;
	}

	/**
	* Set the value of imageDetails.
	* @param imageDetails The list of images that has to be updated.
	*/
	public void setImageDetails(List<ImageDetails> imageDetails) {
		this.imageDetails = imageDetails;
	}

	/**
	* Get the value of imageDetails.
	* @return The list of images that has to be updated.
	*/
	public List<ImageDetails> getImageDetails() {
		return imageDetails;
	}

	/**
	* Add an image to the list that has to be saved.
	* @param imageDetails The image that has to be added to the list.
	*/
	public void addImage(ImageDetails imageDetails) {
		this.imageDetails.add(imageDetails);
	}

	@Override
	public String toString() {
		String id = "";

		if (imageDetails != null)
			for (ImageDetails imageDetails : this.imageDetails) {
				id += "\t\t" + imageDetails.getTopic() + " (" + imageDetails.getSource() + "): " + imageDetails.getUrl() + "\n";
			}

		return "ImageResponse {\n" + 
			"\timageDetails: [\n" + id + "\t]\n" +
		"}";
	}
}