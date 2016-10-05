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
	private GmailRepository gmailRepository;

	public BusinessListener(GmailRepository gmailRepository) {
		this.gmailRepository = gmailRepository;
	}

	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) {
		System.out.println("Received: " + authCode);
		GmailPollingUser pollingUser = gmailRepository.findByUserId(authCode.getId());

		if (authCode == null || authCode.equals(""))
			pollingUser.setCurrentlyPolling(false);
		else
			if (pollingUser == null || !pollingUser.getCurrentlyPolling()) {
				try {
					Poller poller = new GmailPoller(gmailRepository, messageBroker, authCode.getAuthCode(), authCode.getId());
					new Thread(poller).start();
				}
				catch (Exception ioe) {
					ioe.printStackTrace();
				}
			}
	}
}
