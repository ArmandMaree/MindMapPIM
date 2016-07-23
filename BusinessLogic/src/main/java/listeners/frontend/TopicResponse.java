package listeners.frontend;

import java.io.Serializable;

/**
* A response for new topics based on a TopicRequest.
*
* @author  Armand Maree
* @since   2016-07-21
* @see TopicRequest
*/
public class TopicResponse implements Serializable {
	private String userId;
	private String[] topics;

	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param topics The array of topics retrieved from the database.
	*/
	public TopicResponse(String userId, String[] topics) {
		this.userId = userId;
		this.topics = topics;
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
	* Get the value of topics.
	* @return The array of topics retrieved from the database.
	*/
	public String[] getTopics() {
		return topics;
	}

	/**
	* Set the value of topics.
	* @param The array of topics retrieved from the database.
	*/
	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	/**
	* Create string representation of TopicRequest for printing
	* @return TopicRequest as a string.
	*/
	@Override
	public String toString() {
		String t = "";

		for (String item : topics) {
			if (t.equals(""))
				t += item;
			else {
				t += "-" + item;
			}
		}

		return "TopicRequest{\n" +
			"\tuserId: " + userId + "\n" +
			"\ttopics: " + t + "\n" +
			"}";
	}
}
