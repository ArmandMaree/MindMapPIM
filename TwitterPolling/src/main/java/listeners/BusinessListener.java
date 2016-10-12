package listeners;

import data.*;
import poller.*;
import repositories.*;
import com.unclutter.poller.*;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class BusinessListener {
	private MessageBroker messageBroker;
	private TwitterRepository twitterRepository;

	public BusinessListener(TwitterRepository twitterRepository) {
		this.twitterRepository = twitterRepository;
	}

	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) throws AlreadyPollingForUserException {
		System.out.println("Received: " + authCode);

		if (authCode.getId().startsWith("stop:")) {
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
