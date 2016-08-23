package data;

import java.io.Serializable;

public class ItemResponse implements Serializable {
	private static final long serialVersionUID = 3312696172406791L;

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

	public String[]  getItems(){
		return items;
	}
	/**
	* Create string representation of ItemResponse for printing
	* @return
	*/
	@Override
	public String toString() {
		return "ItemResponse {\n" +
			"\titems size: \n" + items.length + "\n" +
		"}";
	}
}
