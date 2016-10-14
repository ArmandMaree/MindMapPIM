package listeners;

import com.unclutter.poller.ItemRequestIdentified;
import com.unclutter.poller.ItemResponseIdentified;
import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageNotSentException;

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
	* Puts the item id and user id for a certain post into an iframe the frontend can display.
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

			if (itemId.contains("_"))
				itemId = itemId.substring(itemId.indexOf("_") + 1, itemId.length());

			items.add("<iframe class=\"facebook-iframe\" src=\"https://www.facebook.com/plugins/post.php?href=https://www.facebook.com/" + userId + "/posts/" + itemId + "/&amp;width=500\"></iframe>");
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
