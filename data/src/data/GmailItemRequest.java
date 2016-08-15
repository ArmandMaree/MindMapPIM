package data;

import java.io.Serializable;

/**
* A response for new topics based on a TopicRequest.
*
* @author  Arno Grobler
* @since   2016-08-13
* @see GmailItemRequest
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
	*/
	public GmailItemRequest(String[] itemIds, String userId) {
		super(itemIds,userId);
	}
}