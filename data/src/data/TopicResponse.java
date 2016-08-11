package data;

import java.io.Serializable;

import data.Topic;

/**
* A response for new topics based on a TopicRequest.
*
* @author  Armand Maree
* @since   2016-07-25
* @see TopicRequest
*/
public class TopicResponse implements Serializable {
	private String userId;
	private String[] topicsText;
	private Topic[] topics;

	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param topics The array of topics retrieved from the database.
	*/
	public TopicResponse(String userId, String[] topicsText, Topic[] topics) {
		this.userId = userId;
		this.topicsText = topicsText;
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
	* Get the value of topicsTest.
	* @return The array of topics retrieved from the database in String form.
	*/
	public String[] getTopicsText() {
		return topicsText;
	}

	/**
	* Set the value of topicsText.
	* @param The array of topics retrieved from the database in String form.
	*/
	public void setTopics(String[] topicsText) {
		this.topicsText = topicsText;
	}

	/**
	* Get the value of topics.
	* @return The array of topics retrieved from the database.
	*/
	public Topic[] getTopics() {
		return topics;
	}

	/**
	* Set the value of topics.
	* @param The array of topics retrieved from the database.
	*/
	public void setTopics(Topic[] topics) {
		this.topics = topics;
	}

	/**
	* Create string representation of TopicRequest for printing
	* @return TopicRequest as a string.
	*/
	@Override
	public String toString() {
		String t = "";

		if (topicsText != null)
			for (String item : topicsText) {
				if (t.equals(""))
					t += item;
				else {
					t += "-" + item;
				}
			}

		return "TopicResponse{\n" +
			"\tuserId: " + userId + "\n" +
			"\ttopics: " + t + "\n" +
			"}";
	}
}
