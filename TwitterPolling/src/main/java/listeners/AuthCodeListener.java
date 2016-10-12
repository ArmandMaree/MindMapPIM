package listeners;

import com.unclutter.poller.MessageBroker;

import data.AuthCode;

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
			String id = authCode.getId().substring(6);
			TwitterPollingUser pollingUser = twitterRepository.findByUserId(id);

			if (pollingUser != null) {
				pollingUser.setCurrentlyPolling(false);
				twitterRepository.save(pollingUser);
			}
		}
		else {
			TwitterPoller poller = new TwitterPoller(twitterRepository, messageBroker, authCode.getId());
			new Thread(poller).start();
		}
	}
}
