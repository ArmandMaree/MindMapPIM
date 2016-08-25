package data;

import java.io.Serializable;

/**
* A request to retrieve a certain email from a user's Gmail account.
*
* @author  Arno Grobler, Armand Maree
* @since   1.0.0
*/
public class GmailItemRequest extends ItemRequest {
	private static final long serialVersionUID = 9152211530232115L;

	/**
	* Default empty GmailItemRequest constructor
	*/
	public GmailItemRequest() {
		super();
	}

	/**
	* GmailItemRequest constructor given by te user
	* @param itemIds An array of the item ids that should be retrieved.
	* @param userId The ID used by the PIM to identify the user.
	*/
	public GmailItemRequest(String[] itemIds, String userId) {
		super(itemIds,userId);
	}
}