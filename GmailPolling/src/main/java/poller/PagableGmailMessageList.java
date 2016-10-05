package poller;

import java.util.*;
import com.google.api.services.gmail.model.*;

/**
* Wrapper object for the emails on one page and the token to the next page on Gmail.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class PagableGmailMessageList {
	/**
	* List of all the emails on some page.
	*/
	public List<Message> messages = null;

	/**
	* Token that can be used to get the next page.
	*/
	public String nextPageToken = null;
}
