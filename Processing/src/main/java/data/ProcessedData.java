package data;

import java.io.Serializable;

/**
* Contains the topics that was extracted from the NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-14
*/
public class ProcessedData implements Serializable {
	/**
	* Indicates the type of PIM that the information was extracted from.
	*/
	private String pimSource = "";

	/**
	* The user ID relevant to the pimSource.
	*/
	private String userId = "";

	/**
	* The IDs of other users involved in this specific list of topics.
	*/
	private String[] involvedContacts = null;

	/**
	* The ID of the item where the topics was extracted, relevant to the pimSource.
	*/
	private String pimItemId = "";

	/**
	* Array of topics as extracted by the NaturalLanguageProcessor.
	*/
	private String[] topics = null;

	/**
	* Constructor that initializes its fields from raw data and new topics.
	* @param rawData The RawData object where the information of the topics were extracted from.
	* @param topics Array of topics as extracted from the data of the rawObject.
	* @see data.RawData
	*/
	public ProcessedData(RawData rawData, String[] topics) {
		this.pimSource = rawData.getPimSource();
		this.userId = rawData.getUserId();
		this.involvedContacts = rawData.getInvolvedContacts();
		this.pimItemId = rawData.getPimItemId();
		this.topics = topics;
	}

	/**
	* Returns value of pimSource
	* @return String name of the PIM the data is from.
	*/
	public String getPimSource() {
		return pimSource;
	}

	/**
	* Sets new value of pimSource
	* @param pimSource String name of the PIM the data is from.
	*/
	public void setPimSource(String pimSource) {
		this.pimSource = pimSource;
	}

	/**
	* Returns value of userId
	* @return String ID of the user w.r.t. the PIM.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Sets new value of userId
	* @param userId String ID of the user w.r.t. the PIM.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns value of involvedContacts
	* @return Array of contacts identified by their ID relevant to the PIM.
	*/
	public String[] getInvolvedContacts() {
		return involvedContacts;
	}

	/**
	* Sets new value of involvedContacts
	* @param involvedContacts Array of contacts identified by their ID relevant to the PIM.
	*/
	public void setInvolvedContacts(String[] involvedContacts) {
		this.involvedContacts = involvedContacts;
	}

	/**
	* Returns value of pimItemId
	* @return String ID of the item the data was extracted from w.r.t. the PIM.
	*/
	public String getPimItemId() {
		return pimItemId;
	}

	/**
	* Sets new value of pimItemId
	* @param pimItemId String ID of the item the data was extracted from w.r.t. the PIM.
	*/
	public void setPimItemId(String pimItemId) {
		this.pimItemId = pimItemId;
	}

	/**
	* Returns value of topics
	* @return
	*/
	public String[] getTopics() {
		return topics;
	}

	/**
	* Sets new value of topics
	* @param topics Array of topics extracted from NaturalLanguageProcessor
	*/
	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	/**
	* Converts the object to a String format used for debugging.
	* @return String The object as a String.
	*/
	@Override
	public String toString() {
		String s = "ProcessedData: {\n" +
			"\tpimSource: " + pimSource + "\n" +
			"\tuserId: " + userId + "\n" +
			"\tinvolvedContacts: [\n";

		if (involvedContacts != null)
			for (String contact : involvedContacts) {
				s += "\t\t" + contact + "\n";
			}
		else
			s += "\t\tnone\n";

		s += "\t]\n" +
			"\tpimItemId: " + pimItemId + "\n" +
			"\ttopics: [\n";

		if (topics != null)
			for (String topic : topics) {
				s += "\t\t" + topic + "\n";
			}
		else
			s += "\t\tnone\n";

		s += "\t]\n}";

		return s;
	}
}
