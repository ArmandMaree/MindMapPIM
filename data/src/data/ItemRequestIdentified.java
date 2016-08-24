package data;

import java.io.Serializable;

/**
* A request to retrieve a certain item from a user's PIM account with an attached returnId.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemRequestIdentified extends ItemRequest implements Serializable {
	private static final long serialVersionUID = 4542718995124476L;

	/**
	* A string that serves as an ID that can allow the requester to find the response that matches their request.
	*/
	private String returnId;

	/**
	* Default constructor.
	*/
	public ItemRequestIdentified() {
		super();
	}

	/**
	* Constructor that initializes some values.
	* @param returnId A string that serves as an ID that can allow the requester to find the response that matches their request.
	* @param itemIds An array of the item ids that should be retrieved.
	* @param userId The ID used by the PIM to identify the user.
	*/
	public ItemRequestIdentified(String returnId, String[] itemIds, String userId) {
		super(itemIds, userId);
		this.returnId = returnId;
	}

	/**
	* Returns the value of returnId.
	* @return A string that serves as an ID that can allow the requester to find the response that matches their request.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Sets the value of returnId.
	* @param returnId A string that serves as an ID that can allow the requester to find the response that matches their request.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Create string representation of ItemRequestIdentified for printing.
	* @return String representation of an ItemRequestIdentified.
	*/
	@Override
	public String toString() {
		String i = "";

		for (String id : getItemIds()) {
			if (i.equals(""))
				i += "\t\t" + id;
			else
				i += ",\n\t\t" + id;
		}

		return "ItemRequest {\n" +
			"\treturnId: " + returnId + ",\n" +
			"\tuserId: [\n" + getUserId() + "\n" +
			"\titemids: [\n" + i + "\n" +
			"\t]\n" +
		"}";
	}
}
