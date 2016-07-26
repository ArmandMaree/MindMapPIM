package data;

import java.io.Serializable;
import org.springframework.data.annotation.Id;

/**
* Contains the topics that was extracted from the NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class ProcessedData implements Serializable {
	/**
	* ID used in database.
	*/
	@Id
	private String id;

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
	* Time the item was received.
	*/
	private long time = 0;

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
		this.time = rawData.getTime();
	}

	/**
	* Default ProcessedData constructor
	* @param id Id used in database.
	* @param pimSource PIM where the data was retrieved from.
	* @param userId Id of the user.
	* @param involvedContacts IDs of the contacts involved.
	* @param pimItemId ID of the item that the information was extracted from.
	* @param topics Array of topics extracted.
	*/
	public ProcessedData(String id, String pimSource, String userId, String[] involvedContacts, String pimItemId, String[] topics, long time) {
		this.id = id;
		this.pimSource = pimSource;
		this.userId = userId;
		this.involvedContacts = involvedContacts;
		this.pimItemId = pimItemId;
		this.topics = topics;
		this.time = time;
	}

	/**
	* Default ProcessedData constructor
	* @param pimSource PIM where the data was retrieved from.
	* @param userId Id of the user.
	* @param involvedContacts IDs of the contacts involved.
	* @param pimItemId ID of the item that the information was extracted from.
	* @param topics Array of topics extracted.
	*/
	public ProcessedData(String pimSource, String userId, String[] involvedContacts, String pimItemId, String[] topics, long time) {
		this.pimSource = pimSource;
		this.userId = userId;
		this.involvedContacts = involvedContacts;
		this.pimItemId = pimItemId;
		this.topics = topics;
		this.time = time;
	}

	/**
	* Returns value of id
	* @return The id in the database.
	*/
	public String getId() {
		return id;
	}

	/**
	* Set the value of Id
	* @param id The id used in the database.
	*/
	public void setId(String id) {
		this.id = id;
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
	* @return Array of topics extracted from item.
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
	* Set the time item was received.
	* @param time The new time.
	*/
	public void setTime(long time) {
		this.time = time;
	}

	/**
	* Returbs the value of time.
	* @return The time the item was received.
	*/
	public long getTime() {
		return time;
	}

	/**
	* Converts the object to a String format used for debugging.
	* @return String The object as a String.
	*/
	@Override
	public String toString() {
		String s = "ProcessedData: {\n" +
			"\tid: " + id + "\n" +
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
