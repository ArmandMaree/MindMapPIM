package listeners;

import data.*;
import poller.*;
import repositories.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

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

	@Autowired
	private GmailRepository gmailRepository;

	public void receiveAuthCode(AuthCode authCode) {
		Poller poller = new GmailPoller(gmailRepository, rabbitTemplate, authCode.getAuthCode(), authCode.getId());
		new Thread(poller).start();
	}
}
