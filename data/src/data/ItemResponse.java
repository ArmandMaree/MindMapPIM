package data;

import java.io.Serializable;

/**
* A response to a request for items from a specific PIM.
*
* @author  Armand Maree
* @since   1.0.0
* @see data.ItemRequest
*/
public class ItemResponse implements Serializable {
	private static final long serialVersionUID = 3312696172406791L;

	/**
	* An array of items that were retrieved from some PIM.
	*/
	private String[] items;

	/**
	* Default constructor
	*/
	public ItemResponse() {
		super();
	}

	/**
	* Constructor that set some member variables.
	* @param items An array of items that were retrieved from some PIM.
	*/
	public ItemResponse(String[] items) {
		super();
		this.items = items;
	}

	/**
	* Returns the value of items.
	* @return An array of items that were retrieved from some PIM.
	*/
	public String[] getItems(){
		return items;
	}

	/**
	* Sets the value of items.
	* @param items An array of items that were retrieved from some PIM.
	*/
	public void setItems(String[] items){
		this.items = items;
	}

	/**
	* Create string representation of ItemResponse for printing.
	* @return String represntation for printing.
	*/
	@Override
	public String toString() {
		return "ItemResponse {\n" +
			"\titems size: \n" + items.length + "\n" +
		"}";
	}
}
