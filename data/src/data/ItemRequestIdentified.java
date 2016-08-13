package data;

import java.io.Serializable;

public class ItemRequestIdentified extends ItemRequest implements Serializable {
	String returnId;

	public ItemRequestIdentified() {
		super();
	}

	public ItemRequestIdentified(String returnId, String[] itemIds, String userId) {
		super(itemIds, userId);
		this.returnId = returnId;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
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
			"\treturnId: " + returnId + ",\n" +
			"\titemids: [\n" + i + "\n" +
			"\t]\n" +
		"}";
	}
}
