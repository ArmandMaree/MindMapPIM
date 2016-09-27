package com.unclutter.poller;

import java.io.Serializable;
import java.util.List;

/**
* Contains the raw text extracted from some PIM.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class RawData implements Serializable {
	private static final long serialVersionUID = 0020554067007013L;

	/**
	* Enables toString to print all the strings in the data array.
	*/
	private boolean verbosePrint = false;

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
	private List<String> involvedContacts = null;

	/**
	* The ID of the item where the topics was extracted, relevant to the pimSource.
	*/
	private String pimItemId = "";

	/**
	* Raw text as extracted from the PIM in pimSource.
	*/
	private String[] data = null;

	/**
	* Time the item was received.
	*/
	private long time = 0;

	/**
	* Default empty RawData constructor
	*/
	public RawData() {
		super();
	}

	/**
	* Default RawData constructor
	* @param pimSource String name of the PIM the data is from.
	* @param userId String ID of the user w.r.t. the PIM.
	* @param involvedContacts Array of contacts identified by their ID relevant to the PIM.
	* @param pimItemId String ID of the item the data was extracted from w.r.t. the PIM.
	* @param data Array of text that was extracted.
	* @param time The time that the item was received by the PIM in milliseconds.
	*/
	public RawData(String pimSource, String userId, List<String> involvedContacts, String pimItemId, String[] data, long time) {
		super();
		this.pimSource = pimSource;
		this.userId = userId;
		this.involvedContacts = involvedContacts;
		this.pimItemId = pimItemId;
		this.data = data;
		this.time = time;
	}

	/**
	* Returns value of verbosePrint
	* @return Value of verbosePrint.
	*/
	public boolean getVerbosePrint() {
		return verbosePrint;
	}

	/**
	* Sets new value of verbosePrint
	* @param verbosePrint true=prints data, false=print count of data
	*/
	public void setVerbosePrint(boolean verbosePrint) {
		this.verbosePrint = verbosePrint;
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
	public List<String> getInvolvedContacts() {
		return involvedContacts;
	}

	/**
	* Sets new value of involvedContacts
	* @param involvedContacts Array of contacts identified by their ID relevant to the PIM.
	*/
	public void setInvolvedContacts(List<String> involvedContacts) {
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
	* Returns value of data
	* @return Array of text that was extracted.
	*/
	public String[] getData() {
		return data;
	}

	/**
	* Sets new value of data
	* @param data Array of text that was extracted.
	*/
	public void setData(String[] data) {
		this.data = data;
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
	* Converts the object to a String format used for debugging and printing.
	* @return The object as a String.
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

		if (verbosePrint) {
			s += "\t]\n" +
				"\tpimItemId: " + pimItemId + "\n" +
				"\tdata: [\n";

			for (String d : data) {
				s += "\t\t" + d + "\n";
			}

			s += "\t]\n}";
		}
		else {
			s += "\t]\n" +
				"\tpimItemId: " + pimItemId + "\n" +
				"\tdataCount: " + ((data == null) ? 0 : data.length) + "\n" +
				"}";
		}

		return s;
	}
}
