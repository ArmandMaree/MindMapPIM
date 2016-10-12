package listeners;

import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageNotSentException;
import com.unclutter.poller.ItemRequestIdentified;
import com.unclutter.poller.ItemResponseIdentified;

import java.util.ArrayList;
import java.util.List;

/**
* Waits for request that requires operations to do with PIM items.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemListener {
	private MessageBroker messageBroker;

	/**
	* Set the value of messageBroker.
	* @param messageBroker Will be passed to the pollers inorder for them to send messages.
	*/
	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Puts the item id and user id for a certain post into an blockquote element the frontend can display.
	* <p>
	*	If the item is for another user then save the itemId like this: "userId:itemId".
	* </p>
	* @param itemRequestIdentified Contains all the relevant user information and the IDs of the posts that needs to be retrieved.
	*/
	public void receiveItemRequest(ItemRequestIdentified itemRequestIdentified) {
		System.out.println("Received: " + itemRequestIdentified);
		List<String> items = new ArrayList<>();

		for (String itemId : itemRequestIdentified.getItemIds()) {
			if (items.size() == 0) {
				items.add(itemId);
				continue;
			}
			
			String userId;

			if (itemId.contains(":")) {
				userId = itemId.substring(itemId.indexOf(":") + 1, itemId.length());
				itemId = itemId.substring(0, itemId.indexOf(":"));
			}
			else
				userId = itemRequestIdentified.getUserId();

			items.add("<blockquote class=\"twitter-tweet\" data-lang=\"en\">" + 
				"<p lang=\"en\" dir=\"ltr\">" +
					"Loading Tweet" +
				"</p>" + 
				"<a href=\"https://twitter.com/" + userId + "/status/" + itemId + "\"></a></blockquote>" +
				"<script async src=\"//platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>");
		}

		ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), items.toArray(new String[items.size()]));
		System.out.println("Responded: " + itemResponseIdentified);

		try {
			messageBroker.sendItem(itemResponseIdentified);	
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}
}
