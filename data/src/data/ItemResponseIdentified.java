package data;

import java.io.Serializable;

/**
* A response to a request for items from a specific PIM.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemResponseIdentified extends ItemResponse implements Serializable {
	private static final long serialVersionUID = 9039743111375242L;

	/**
	* A string that serves as an ID that can allow the requester to find the response that matches their request.
	*/
	private String returnId;

	/**
	* Default constructor.
	*/
	public ItemResponseIdentified() {
		super();
	}

	/**
	* Constructor that initializes some member variables.
	* @param returnId A string that serves as an ID that can allow the requester to find the response that matches their request.
	* @param items An array of items that were retrieved from some PIM.
	*/
	public ItemResponseIdentified(String returnId, String[] items) {
		super(items);
		this.returnId = returnId;
	}

	/**
	* Returns the value of returnId.
	* @return An array of items that were retrieved from some PIM.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Sets the value of returnId.
	* @param returnId An array of items that were retrieved from some PIM.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Create string representation of ItemResponseIdentified for printing.
	* @return String representation of ItemResponseIdentified.
	*/
	@Override
	public String toString() {
		String i = "";

		for (String id : getItems()) {
			if (i.equals(""))
				i += "\t\t" + id;
			else
				i += ",\n\t\t" + id;
		}

		return "ItemResponseIdentified {\n" +
			"\treturnId: " + returnId + ",\n" +
			"\titems size: " + getItems().length + "\n" +
		"}";
	}
}
