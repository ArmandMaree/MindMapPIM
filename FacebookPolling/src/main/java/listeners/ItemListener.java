package listeners;

import com.unclutter.poller.ItemRequestIdentified;
import com.unclutter.poller.ItemResponseIdentified;
import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageNotSentException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.connect.FacebookServiceProvider;

import poller.FacebookPollingUser;

import repositories.FacebookRepository;

/**
* Waits for request that requires operations to do with PIM items.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemListener {
	private MessageBroker messageBroker;
	private FacebookRepository facebookRepository;

	public ItemListener(FacebookRepository facebookRepository) {
		this.facebookRepository = facebookRepository;
	}

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
	*	If the item is for another user then save the itemId like this: "itemId:userId".
	* </p>
	* @param itemRequestIdentified Contains all the relevant user information and the IDs of the posts that needs to be retrieved.
	*/
	public void receiveItemRequest(ItemRequestIdentified itemRequestIdentified) {
		try {
			System.out.println("Received: " + itemRequestIdentified);
			List<String> items = new ArrayList<>();
			FacebookPollingUser pollingUser = facebookRepository.findByUserId(itemRequestIdentified.getUserId());

			if (pollingUser == null) {
				ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), new String[0]);
				System.out.println("Responded: " + itemResponseIdentified);
				return;
			}

			Facebook service = getService(pollingUser.getAccessToken());

			for (String itemId : itemRequestIdentified.getItemIds()) {
				try {
					if (items.size() == 0) {
						items.add(itemId);
						continue;
					}
					
					String userId;
					String privacy = "";

					if (itemId.contains(":")) {
						privacy = itemId.substring(0, itemId.indexOf(":"));
						itemId = itemId.substring(itemId.indexOf(":") + 1, itemId.length());

						if (itemId.contains(":")) {
							userId = itemId.substring(itemId.indexOf(":") + 1, itemId.length());
							itemId = itemId.substring(0, itemId.indexOf(":"));
						}
						else
							userId = itemRequestIdentified.getUserId();
					}
					else
						userId = itemRequestIdentified.getUserId();


					Post post = service.feedOperations().getPost(itemId);

					if (privacy.equals("EVERYONE") || post.getMessage() == null) {
						if (itemId.contains("_"))
							itemId = itemId.substring(itemId.indexOf("_") + 1, itemId.length());
						
						items.add("<iframe class=\"facebook-iframe\" src=\"https://www.facebook.com/plugins/post.php?href=https://www.facebook.com/" + userId + "/posts/" + itemId + "/&amp;width=500\"></iframe>");
					}
					else
						items.add(post.getMessage());

					// String userId;

					// if (itemId.contains(":")) {
					// 		userId = itemId.substring(itemId.indexOf(":") + 1, itemId.length());
					// 		itemId = itemId.substring(0, itemId.indexOf(":"));
					// 	}
					// 	else
					// 		userId = itemRequestIdentified.getUserId();

					// items.add("<iframe class=\"facebook-iframe\" src=\"https://www.facebook.com/plugins/post.php?href=https://www.facebook.com/" + userId + "/posts/" + itemId + "/&amp;width=500\"></iframe>");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), items.toArray(new String[items.size()]));
			System.out.println("Responded: " + itemResponseIdentified);

			messageBroker.sendItem(itemResponseIdentified);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* Creates am authenticated Facebook service.
	* @param authCode The access token that must be used to authenticate.
	* @return An authenticated Facebook service.
	*/
	public Facebook getService(String authCode) {
		String REDIRECT_URI = "https://bubbles.iminsys.com/";
		FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider("1051696778242173", "22a06683d76460f1522396944e7e0506", "datamine");
		return facebookServiceProvider.getApi(authCode);
	}
}
