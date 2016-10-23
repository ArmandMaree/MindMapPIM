package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageSaveRequest implements Serializable {
	private static final long serialVersionUID = 5912853921916622L;

	private List<ImageDetails> imageDetails;

	/**
	* Default constructor.
	*/
	public ImageSaveRequest() {
		imageDetails = new ArrayList<>();
	}

	/**
	* Constructor.
	* @param imageDetails The images that has to be saved.
	*/
	public ImageSaveRequest(List<ImageDetails> imageDetails) {
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

	/**
	* Get a string representation of this ImageRequestIdentified object used for printing.
	* @return A string reprentation of this object.
	*/
	@Override
	public String toString() {
		String id = "";

		if (imageDetails != null)
			for (ImageDetails imageDetails : this.imageDetails) {
				id += "\t\t" + imageDetails.getTopic() + " (" + imageDetails.getSource() + "): " + imageDetails.getUrl() + "\n";
			}

		return "ImageSaveRequest {\n" + 
			"\timageDetails: [\n" + id + "\t]\n" +
		"}";
	}
}