package listeners;

import java.io.Serializable;

/**
* A request for new topics based on a given path.
*
* @author  Armand Maree
* @since   2016-07-21
*/
public class TopicRequest implements Serializable {
	private String userId;
	private String[] path;
	private String[] exclude;
	private int maxNumberOfTopics;

	/**
	* Default constructor.
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
	* @param The array of topics that must be included in the result topics.
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
	* @param The array of topics that must not be included in the result topics.
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
