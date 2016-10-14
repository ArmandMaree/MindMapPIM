package listeners;

import com.unclutter.poller.MessageBroker;

import data.AuthCode;

import java.util.ArrayList;
import java.util.List;

import poller.AlreadyPollingForUserException;
import poller.FacebookPoller;
import poller.FacebookPollingUser;

import repositories.FacebookRepository;

/**
* Waits for {@link data.AuthCode} messages. Usually for poller starting.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class AuthCodeListener {
	private MessageBroker messageBroker;
	private FacebookRepository facebookRepository;
	private static List<FacebookPoller> facebookPollers = new ArrayList<>();

	/**
	* Constructor.
	* @param facebookRepository The repository that will be used to persist information of each user that is being polled for. {@link poller.FacebookPollingUser}
	*/
	public AuthCodeListener(FacebookRepository facebookRepository) {
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
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* <p>
	*	If the {@link data.AuthCode#authCode} is null or an empty string then the poller will be stopped that is running for a user with the ID corresponding to {@link data.AuthCode#id}.
	* </p>
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) throws AlreadyPollingForUserException {
		System.out.println("Received: " + authCode);

		if (authCode.getAuthCode() == null || authCode.getAuthCode().equals("")) {
			FacebookPollingUser pollingUser = facebookRepository.findByUserId(authCode.getId());

			if (pollingUser != null) {
				for (FacebookPoller facebookPoller : facebookPollers)
					if (facebookPoller.getUserId().equals(authCode.getId()))
						facebookPoller.stopPoller();
			}
		}
		else {
			FacebookPoller facebookPoller = new FacebookPoller(facebookRepository, messageBroker, authCode.getAuthCode(), authCode.getExpireTime(), authCode.getId());
			facebookPollers.add(facebookPoller);
			new Thread(facebookPoller).start();
		}
	}
}
