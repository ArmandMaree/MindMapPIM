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
}
