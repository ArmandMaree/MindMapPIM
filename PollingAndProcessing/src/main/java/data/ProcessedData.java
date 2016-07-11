package data;

/**
* Contains the topics that was extracted from the NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-11
*/
public class ProcessedData {
	/**
	* Indicates the type of PIM that the information was extracted from.
	*/
	public String pimSource = "";

	/**
	* The user ID relevant to the pimSource.
	*/
	public String userId = "";

	/**
	* The IDs of other users involved in this specific list of topics.
	*/
	public String[] involvedContacts = null;

	/**
	* The ID of the item where the topics was extracted, relevant to the pimSource.
	*/
	public String pimItemId = "";

	/**
	* Array of topics as extracted by the NaturalLanguageProcessor.
	*/
	public String[] topics = null;

	/**
	* Constructor that initializes its fields from raw data and new topics.
	* @param rawData The RawData object where the information of the topics were extracted from.
	* @param topics Array of topics as extracted from the data of the rawObject.
	* @see data.RawData
	*/
	public ProcessedData(RawData rawData, String[] topics) {
		this.pimSource = rawData.pimSource;
		this.userId = rawData.userId;
		this.involvedContacts = rawData.involvedContacts;
		this.pimItemId = rawData.pimItemId;
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