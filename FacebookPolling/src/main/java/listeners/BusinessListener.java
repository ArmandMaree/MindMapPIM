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
	private FacebookRepository facebookRepository;

	public BusinessListener(FacebookRepository facebookRepository) {
		this.facebookRepository = facebookRepository;
	} 

	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) throws AlreadyPollingForUserException {
		System.out.println("Starting for: " + authCode);
		FacebookPoller poller = new FacebookPoller(facebookRepository, messageBroker, authCode.getAuthCode(), authCode.getExpireTime(), authCode.getId());
		new Thread(poller).start();
	}
}
