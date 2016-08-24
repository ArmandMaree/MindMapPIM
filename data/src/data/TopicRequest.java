package data;

import java.io.Serializable;

/**
* A request for new topics based on a given path.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class TopicRequest implements Serializable {
	private static final long serialVersionUID = 732667706236639L;

	/**
	* The Id of the user as used by the database.
	*/
	private String userId;

	/**
	* The path that was followed to obtain this node in the mindmap.
	* <p>
	*	To get the root node's related topics, make the path null or empty.<br>
	*	The path will automatically be excluded from the return topics.
	* </p>
	*/
	private String[] path;

	/**
	* The topics that should be excluded from the topics that will be returned.
	*/
	private String[] exclude;

	/**
	* The maximim number of topics and contacts that may be returned.
	*/
	private int maxNumberOfTopics;

	/**
	* Default constructor.
	*/
	public TopicRequest(){

	}
	/**
	* Constructor that initializes some variables.
	* @param userId the id of the user the request is for.
	* @param path The array of topics that has to be used to get new topics from.
	* @param exclude A list of topics that should not be returned.
	* @param maxNumberOfTopics The maximum nuber of topics that may be returned.
	*/
	public TopicRequest(String userId, String[] path, String[] exclude, int maxNumberOfTopics) {
		this.userId = userId;
		this.path = path;
		this.exclude = exclude;
		this.maxNumberOfTopics = maxNumberOfTopics;
	}

	/**
	* Get the value of userId.
	* @return The id of the user the request is for.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Set the value of userId
	* @param userId The id of the user the request is for.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Get the value of path.
	* @return The array of topics that must be included in the result topics.
	*/
	public String[] getPath() {
		return path;
	}

	/**
	* Set the value of path.
	* @param path The array of topics that must be included in the result topics.
	*/
	public void setPath(String[] path) {
		this.path = path;
	}

	/**
	* Get the value of exclude.
	* @return The array of topics that must not be included in the result topics.
	*/
	public String[] getExclude() {
		return exclude;
	}

	/**
	* Set the value of exclude.
	* @param exclude The array of topics that must not be included in the result topics.
	*/
	public void setExclude(String[] exclude) {
		this.exclude = exclude;
	}

	/**
	* Get the value of maxNumberOfTopics
	* @return The maximum number of topics in the response.
	*/
	public int getMaxNumberOfTopics() {
		return maxNumberOfTopics;
	}

	/**
	* Set the value of maxNumberOfTopics.
	* @param maxNumberOfTopics The maximum number of topics in the response.
	*/
	public void setMaxNumberOfTopics(int maxNumberOfTopics) {
		this.maxNumberOfTopics = maxNumberOfTopics;
	}

	/**
	* Create string representation of TopicRequest for printing
	* @return TopicRequest as a string.
	*/
	@Override
	public String toString() {
		String p = "";

		if (path != null)
			for (String item : path) {
				if (p.equals(""))
					p += item;
				else {
					p += "-" + item;
				}
			}

		String e = "";

		if (exclude != null)
			for (String item : exclude) {
				if (e.equals(""))
					e += item;
				else {
					e += "-" + item;
				}
			}

		return "TopicRequest{\n" +
			"\tuserId: " + userId + "\n" +
			"\tpath: " + p + "\n" +
			"\texclude: " + e + "\n" +
			"\tmaxNumberOfTopics: " + maxNumberOfTopics + "\n" +
			"}";
	}
}
