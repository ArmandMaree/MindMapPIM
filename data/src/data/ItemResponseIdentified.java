package data;

import java.io.Serializable;

public class ItemResponseIdentified extends ItemResponse implements Serializable {
	private static final long serialVersionUID = 9039743111375242L;

	String returnId;

	/**
	* Default empty ItemResponseIdentified constructor
	*/
	public ItemResponseIdentified() {
		super();
	}

	/**
	* Default ItemResponseIdentified constructor
	*/
	public ItemResponseIdentified(String returnId, String[] items) {
		super(items);
		this.returnId = returnId;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}
	
	public String[]  getItems(){
		return items;
	}
	/**
	* Create string representation of ItemResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		String i = "";

		for (String id : items) {
			if (i.equals(""))
				i += "\t\t" + id;
			else
				i += ",\n\t\t" + id;
		}

		return "ItemResponse {\n" +
			"\treturnId: " + returnId + ",\n" +
			"\titems size: \n" + items.length + "\n" +
		"}";
	}
}
