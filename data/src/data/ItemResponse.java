package data;

import java.io.Serializable;

public class ItemResponse implements Serializable {
	String[] items;

	/**
	* Default empty ItemResponse constructor
	*/
	public ItemResponse() {
		super();
	}

	/**
	* Default ItemResponse constructor
	*/
	public ItemResponse(String[] items) {
		super();
		this.items = items;
	}
}
