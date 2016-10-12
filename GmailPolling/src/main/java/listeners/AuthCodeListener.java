package listeners;

import com.unclutter.poller.MessageBroker;

import data.AuthCode;

import poller.GmailPoller;
import poller.GmailPollingUser;

import repositories.GmailRepository;

/**
* Waits for {@link data.AuthCode} messages. Usually for poller starting.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class AuthCodeListener {
	private MessageBroker messageBroker;
	private GmailRepository gmailRepository;

	/**
	* Constructor.
	* @param gmailRepository The repository that will be used to persist information of each user that is being polled for. {@link poller.GmailPollingUser}
	*/
	public AuthCodeListener(GmailRepository gmailRepository) {
		this.gmailRepository = gmailRepository;
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
	public void receiveAuthCode(AuthCode authCode) {
		System.out.println("Received: " + authCode);
		GmailPollingUser pollingUser = gmailRepository.findByUserId(authCode.getId());

		if (authCode == null || authCode.equals(""))
			pollingUser.setCurrentlyPolling(false);
		else
			if (pollingUser == null || !pollingUser.getCurrentlyPolling()) {
				try {
					GmailPoller poller = new GmailPoller(gmailRepository, messageBroker, authCode.getAuthCode(), authCode.getId());
					new Thread(poller).start();
				}
				catch (Exception ioe) {
					ioe.printStackTrace();
				}
			}
	}
}
