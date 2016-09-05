package data;

import java.io.Serializable;

import data.Topic;

/**
* A response for new topics based on a TopicRequest.
*
* @author  Armand Maree
* @since   1.0.0
* @see data.TopicRequest
*/
public class TopicResponse implements Serializable {
	private static final long serialVersionUID = 1479600095184605L;

	private String userId;
	private String[] topicsText;
	private String[] involvedContacts;
	private String[][][] pimSourceIds;
	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param topicsText The array of topics retrieved from the database in String form.
	* @param involvedContacts The array of contacts retrieved from the database in String form.
	* @param pimSourceIds The array of ids for the ids of the items used by the pims.
	*/
	public TopicResponse(String userId, String[] topicsText, String[] involvedContacts, String[][][] pimSourceIds) {
		this.userId = userId;
		this.topicsText = topicsText;
		this.pimSourceIds = pimSourceIds;
		this.involvedContacts = involvedContacts;
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
	* Get the value of topicsText.
	* @return The array of topics retrieved from the database in String form.
	*/
	public String[] getTopicsText() {
		return topicsText;
	}


	/**
	* Set the value of topicsText.
	* @param topicsText The array of topics retrieved from the database in String form.
	*/
	public void setTopics(String[] topicsText) {
		this.topicsText = topicsText;
	}

	/**
	* Get the value of involvedContacts.
	* @return The array of topics retrieved from the database in String form.
	*/
	public String[] getInvolvedContacts() {
		return involvedContacts;
	}
	
	/**
	* Set the value of involvedContacts.
	* @param involvedContacts The array of contacts retrieved from the database in String form.
	*/
	public void setInvolvedContacts(String[] involvedContacts) {
		this.involvedContacts = involvedContacts;
	}

	/**
	* Get the value of pimSourceIds.
	* @return The array of ids for the ids of the items used by the pims.
	*/
	public String[][][] getPimSourceIds() {
		return pimSourceIds;
	}

	/**
	* Set the value of pimSourceIds.
	* @param pimSourceIds The array of ids for the ids of the items used by the pims.
	*/
	public void setPimSourceIds(String[][][] pimSourceIds) {
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

		String c = "";

		if (involvedContacts != null)
			for (String contact : involvedContacts) {
				if (c.equals(""))
					c += contact;
				else {
					c += "-" + contact;
				}
			}
		else
			c = null;

		String p = "";

		if (pimSourceIds != null && pimSourceIds.length != 0) {
			for (int i = 0; i < pimSourceIds.length; i++) { // iterate nodes
				p += "\t\t[" + i + "]:";

				if (pimSourceIds[i] != null && pimSourceIds[i].length != 0) {
					for (int j = 0; j < pimSourceIds[i].length; j++) { // iterate pims
						if (pimSourceIds[i] != null && pimSourceIds[i].length != 0) {
							if (j == 0)
								p += "\t[";
							else
								p += "\t\t\t[";

							for (int k = 0; k < pimSourceIds[i][j].length; k++) { // iterate ids
								if (k != 0)
									p += "," + pimSourceIds[i][j][k];
								else
									p += pimSourceIds[i][j][k];
							}

							p += "]\n";
						}
						else
							p += "null or empty\n";
					}
				}
				else
					p += "null or empty\n";
			}
		}
		else
			p = "null or empty";

		return "TopicResponse {\n" +
			"\tuserId: " + userId + "\n" +
			"\ttopics: " + t + "\n" +
			"\tinvolvedContacts: " + c + "\n" +
			"\tpimSourceIds: {\n" + p + "\t}\n" +
		"}";
	}
}
