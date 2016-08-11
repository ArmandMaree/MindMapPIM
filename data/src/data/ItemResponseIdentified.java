package data;

import java.io.Serializable;

public class ItemResponseIdentified extends ItemResponse implements Serializable {
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
}
