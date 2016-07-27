package listeners;

import data.*;
import poller.*;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class BusinessListener {
	public void receiveAuthCode(AuthCode authCode) {
		Poller poller = new GmailPoller(authCode.getAuthCode());
		new Thread(poller).start();
	}
}
