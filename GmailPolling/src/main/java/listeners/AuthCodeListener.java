package listeners;

import com.unclutter.poller.MessageBroker;

import data.AuthCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import poller.AlreadyPollingForUserException;
import poller.GmailPoller;
import poller.GmailPollingUser;
import poller.UserNotFoundException;

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
	private static List<GmailPoller> gmailPollers = new ArrayList<>();

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

		if (authCode.getAuthCode() == null || authCode.getAuthCode().equals("")) {
			GmailPollingUser pollingUser = gmailRepository.findByUserId(authCode.getId());

			if (pollingUser != null) {
				for (GmailPoller gmailPoller : gmailPollers)
					if (gmailPoller.getUserId().equals(authCode.getId()))
						gmailPoller.stopPoller();
			}
		}
		else {
			try {
				GmailPoller gmailPoller = new GmailPoller(gmailRepository, messageBroker, authCode.getAuthCode(), authCode.getId());
				gmailPollers.add(gmailPoller);
				new Thread(gmailPoller).start();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (UserNotFoundException unfe) {
				unfe.printStackTrace();
			}
			catch (AlreadyPollingForUserException apfue) {
				apfue.printStackTrace();
			}
		}
	}
}
