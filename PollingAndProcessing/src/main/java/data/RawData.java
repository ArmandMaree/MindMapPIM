package data;

/**
* Contains the raw text extracted from some PIM.
*
* @author  Armand Maree
* @since   2016-07-11
*/
public class RawData {
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
	* Raw text as extracted from the PIM in pimSource.
	*/
	public String[] data = null;

	/**
	* Converts the object to a String format used for debugging.
	* @return String The object as a String.
	*/
	@Override
	public String toString() {
		String s = "RawData: {\n" +
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
			"\tdataCount: " + ((data == null) ? 0 : data.length) + "\n" +
			"}";

		return s;
	}
}