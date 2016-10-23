package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private ImageDetails[] topics;
	private String[] involvedContacts;
	private String[][][] pimSourceIds;

	/**
	* Default constructor.
	* @param userId the id of the user the request is for.
	* @param topics The array of images for the topics in the topicText array.
	* @param involvedContacts The array of contacts retrieved from the database in String form.
	* @param pimSourceIds The array of ids for the ids of the items used by the pims.
	*/
	public TopicResponse(String userId, ImageDetails[] topics, String[] involvedContacts, String[][][] pimSourceIds) {
		this.userId = userId;
		this.topics = topics;
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
	* Get the value of topics.
	* @return The array of images for the topics in the topicText array.
	*/
	public ImageDetails[] getTopics() {
		return topics;
	}

	/**
	* Set the value of topics.
	* @param topics The array of images for the topics in the topicText array.
	*/
	public void setTopics(ImageDetails[] topics) {
		this.topics = topics;
	}

	/**
	* Get the value of involvedContacts.
	* @return The array of contacts retrieved from the database in String form.
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
		String id = "";

		if (topics != null) {
			for (ImageDetails t : topics) {
				id += "\t\t" + t.getTopic() + " (" + t.getSource() + "): " + t.getUrl() + "\n";
			}
		}
		else
			id = "\t\tNULL\n";

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
							p += "\t\tnull or empty\n";
					}
				}
				else
					p += "\t\tnull or empty\n";
			}
		}
		else
			p = "\t\tnull or empty\n";

		return "TopicResponse {\n" +
			"\tuserId: " + userId + "\n" +
			"\ttopics: [\n" + id + "\t]\n" +
			"\tinvolvedContacts: " + c + "\n" +
			"\tpimSourceIds: [\n" + p + "\t]\n" +
		"}";
	}
}
