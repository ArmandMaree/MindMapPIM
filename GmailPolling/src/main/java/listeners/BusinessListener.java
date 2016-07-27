package listeners;

import data.*;
import poller.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class BusinessListener {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void receiveAuthCode(AuthCode authCode) {
		System.out.println("Gmail Received: " + authCode);
		Poller poller = new GmailPoller(rabbitTemplate, authCode.getAuthCode());
		new Thread(poller).start();
	}
}
