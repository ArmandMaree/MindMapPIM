package listeners;

import data.*;
import poller.*;

import java.util.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the frontend service.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FrontendListener {
	private final String itemResponseQueueName = "item-response.frontend.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	* Puts the item id and user id for a certain post into an iframe the frontend can display.
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
				userId = itemId.substring(itemId.indexOf(":"), itemId.length());
				itemId = itemId.substring(0, itemId.indexOf(":"));
			}
			else
				userId = itemRequestIdentified.getUserId();

			items.add("<iframe class=\"facebook-iframe\" src=\"https://www.facebook.com/plugins/post.php?href=https://www.facebook.com/" + userId + "/posts/" + itemId + "/&amp;width=500\"></iframe>");
		}

		ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), items.toArray(new String[items.size()]));
		System.out.println("Responded: " + itemResponseIdentified);
		rabbitTemplate.convertAndSend(itemResponseQueueName, itemResponseIdentified);
	}
}
