package data;

import java.io.Serializable;

public class ItemRequest implements Serializable {
	private static final long serialVersionUID = 9152211530232119L;

	String[] itemIds;
	String userId;

	public ItemRequest() {
		super();
	}

	public ItemRequest(String[] itemIds, String userId) {
		this.itemIds = itemIds;
		this.userId = userId;
	}

	public String[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(String[] itemIds) {
		this.itemIds = itemIds;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Create string representation of ItemRequest for printing
	* @return
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
			"\titemids: [\n" + i + "\n" +
			"\t]\n" +
		"}";
	}
}
