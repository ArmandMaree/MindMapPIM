package data;

import java.util.ArrayList;
import java.util.List;

/**
* This class contains all the topics for which image urls need to be retrieved.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ImageRequest {
	private static final long serialVersionUID = 6435312483721195L;

	private List<String> topics;
	private String source;

	/**
	* Default constructor.
	*/
	public ImageRequest() {
		topics = new ArrayList<>();
		source = null;
	}

	/**
	* Constructor if no source is required.
	* @param topics The list of topics for which images are required.
	*/
	public ImageRequest(List<String> topics) {
		this.topics = topics;
		source = null;
	}

	/**
	* Constructor if a source is required.
	* @param topics The list of topics for which images are required.
	* @param source The source where images must be retrieved from (like google or bing). Null if any source is fine. 
	*/
	public ImageRequest(List<String> topics, String source) {
		this.topics = topics;
		this.source = source;
	}

	/**
	* Set the value of topics.
	* @param topics The list of topics for which images are required.
	*/
	public void setTopics(List<String> topics) {
		this.topics = topics;
	}

	/**
	* get the value of topics.
	* @return The list of topics for which images are required.
	*/
	public List<String> getTopics() {
		return topics;
	}

	/**
	* Set the value of source.
	* @param source The source where images must be retrieved from (like google or bing). Null if any source is fine. 
	*/
	public void setSource(String source) {
		this.source = source.toLowerCase();
	}

	/**
	* Set the value of source.
	* @return The source where images must be retrieved from (like google or bing). Null if any source is fine. 
	*/
	public String getSource() {
		return source;
	}

	/**
	* Add a topic to the list of topics for which images must be retrieved.
	* @param topic The topic that must be added to the list.
	*/
	public void addTopic(String topic) {
		topics.add(topic);
	}

	/**
	* Get a string representation of this ImageRequest object used for printing.
	* @return A string reprentation of this object.
	*/
	@Override
	public String toString() {
		String t = "";

		for (String tS : topics) {
			t += "\t\t" + tS + "\n";
		}

		return "ImageRequest {\n" +
			"\tsource: " + source + "\n" +
			"\ttopics: [" + t + "\t]\n" +
		"}";
	}
}