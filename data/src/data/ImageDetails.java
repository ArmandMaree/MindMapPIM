package data;

import java.io.Serializable;

public class ImageDetails implements Serializable {
	private static final long serialVersionUID = 8918345105947968L;

	private String topic;
	private String url;
	private String source;

	/**
	* Default constructor. Should only be used by deserializers.
	*/
	public ImageDetails() {
		topic = null;
		url = null;
		source = null;
	}

	/**
	* Constructor.
	* @param topic The topic that describes the image conatined in this url.
	*/
	public ImageDetails(String topic) {
		this.topic = topic;
		url = null;
		source = null;
	}

	/**
	* The recomended constructor.
	* @param topic The topic that describes the image conatined in this url.
	* @param url The URL that leads to the image.
	* @param source The source (like Google) that was used to obtained the URL.
	*/
	public ImageDetails(String topic, String url, String source) {
		this.topic = topic;
		this.url = url;
		this.source = source;
	}

	/**
	* Set the value of topic.
	* @param topic The topic that describes the image conatined in this url.
	*/
	public void setTopic(String topic) {
		this.topic = topic;
	}	

	/**
	* Get the value of topic.
	* @return The topic that describes the image conatined in this url.
	*/
	public String getTopic() {
		return topic;
	}

	/**
	* Set the value of url.
	* @param url The URL that leads to the image.
	*/
	public void setUrl(String url) {
		this.url = url;
	}	

	/**
	* Get the value of url.
	* @return The URL that leads to the image.
	*/
	public String getUrl() {
		return url;
	}

	/**
	* Set the value of source.
	* @param source The source (like Google) that was used to obtained the URL.
	*/
	public void setSource(String source) {
		this.source = source;
	}	

	/**
	* Get the value of source.
	* @return The source (like Google) that was used to obtained the URL.
	*/
	public String getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "ImageDetails {\n" + 
			"\ttopic: " + topic + "\n" +
			"\turl: " + url + "\n" +
			"\tsource: " + source + "\n" +
		"}";
	}
}