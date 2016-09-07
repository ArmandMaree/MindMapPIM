package data;

import java.io.Serializable;

/**
* A request to retrieve a certain item from a user's PIM account.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemRequest implements Serializable {
	private static final long serialVersionUID = 3590077352986152L;

	/**
	* An array of the item ids that should be retrieved.
	*/
	private String[] itemIds;

	/**
	* The ID used by the PIM to identify the user.
	*/
	private String userId;

	/**
	* Default constructor.
	*/
	public ItemRequest() {
		super();
	}

	/**
	* Constructor that initializes some member variables.
	* @param itemIds An array of the item ids that should be retrieved.
	* @param userId The ID used by the PIM to identify the user.
	*/
	public ItemRequest(String[] itemIds, String userId) {
		this.itemIds = itemIds;
		this.userId = userId;
	}

	/**
	* Returns the value of userId.
	* @return The ID used by the PIM to identify the user.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Set the value of userId.
	* @param userId The ID used by the PIM to identify the user.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns the value of itemIds.
	* @return An array of the item ids that should be retrieved.
	*/
	public String[] getItemIds() {
		return itemIds;
	}

	/**
	* Set the value of itemIds.
	* @param itemIds An array of the item ids that should be retrieved.
	*/
	public void setItemIds(String[] itemIds) {
		this.itemIds = itemIds;
	}

	/**
	* Create string representation of ItemRequest for printing.
	* @return String representation of an ItemRequest.
	*/
	@Override
	public String toString() {
		String i = "";

		for (String id : itemIds) {
			if (i.equals(""))
				i += "\t\t" + id;
			else
				i += ",\n\t\t" + id;
		}

		return "ItemRequest {\n" +
			"\tuserId: " + userId + "\n" +
			"\titemids: [\n" + i + "\n" +
			"\t]\n" +
		"}";
	}
}
