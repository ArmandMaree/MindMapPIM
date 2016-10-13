package listeners;

import com.unclutter.poller.MessageBroker;

import data.AuthCode;

import java.util.ArrayList;
import java.util.List;

import poller.AlreadyPollingForUserException;
import poller.TwitterPoller;
import poller.TwitterPollingUser;

import repositories.TwitterRepository;

/**
* Waits for {@link data.AuthCode} messages. Usually for poller starting.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class AuthCodeListener {
	private MessageBroker messageBroker;
	private TwitterRepository twitterRepository;
	private static List<TwitterPoller> twitterPollers = new ArrayList<>();

	/**
	* Constructor.
	* @param twitterRepository The repository that will be used to persist information of each user that is being polled for. {@link poller.TwitterPollingUser}
	*/
	public AuthCodeListener(TwitterRepository twitterRepository) {
		this.twitterRepository = twitterRepository;
	}

	/**
	* Set the value of messageBroker.
	* @param messageBroker Will be passed to the pollers inorder for them to send messages.
	*/
	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* <p>
	*	If the {@link data.AuthCode#authCode} is null or an empty string then the poller will be stopped that is running for a user with the ID corresponding to {@link data.AuthCode#id}.
	* </p>
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) throws AlreadyPollingForUserException {
		System.out.println("Received: " + authCode);

		if (authCode.getAuthCode() == null || authCode.getAuthCode().equals("")) {
			TwitterPollingUser pollingUser = twitterRepository.findByUserId(authCode.getId());

			if (pollingUser != null) {
				for (TwitterPoller twitterPoller : twitterPollers)
					if (twitterPoller.getUserId().equals(authCode.getId()))
						twitterPoller.stopPoller();
			}
		}
		else {
			TwitterPoller twitterPoller = new TwitterPoller(twitterRepository, messageBroker, authCode.getAuthCode(), authCode.getExpireTime(), authCode.getId());
			twitterPollers.add(twitterPoller);
			new Thread(twitterPoller).start();
		}
	}
}
