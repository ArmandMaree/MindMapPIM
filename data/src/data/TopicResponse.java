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
	private String[][] pimSourceIds;

	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param pimSourceIds The array of ids for the ids of the items used by the pims.
	*/
	public TopicResponse(String userId, String[] topicsText, String[][] pimSourceIds) {
		this.userId = userId;
		this.topicsText = topicsText;
		this.pimSourceIds = pimSourceIds;
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
	* Get the value of pimSourceIds.
	* @return The array of ids for the ids of the items used by the pims.
	*/
	public String[][] getPimSourceIds() {
		return pimSourceIds;
	}

	/**
	* Set the value of pimSourceIds.
	* @param pimSourceIds The array of ids for the ids of the items used by the pims.
	*/
	public void setPimSourceIds(String[][] pimSourceIds) {
		this.pimSourceIds = pimSourceIds;
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

		String p = "";

		if (pimSourceIds != null)
			for (String[] pimIds : pimSourceIds) {
				p += "\t\t[";

				for (String id : pimIds) {
					if (p.endsWith("["))
						p += id;
					else
						p += ", " + id;
				}

				p += "]\n";
			}

		return "TopicResponse{\n" +
			"\tuserId: " + userId + "\n" +
			"\ttopics: " + t + "\n" +
			"\tpimSourceIds: \n" + p + 
			"}";
	}
}
