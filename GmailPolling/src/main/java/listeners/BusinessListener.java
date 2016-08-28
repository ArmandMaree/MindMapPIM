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
		PollingUser pollingUser = gmailRepository.findByUserId(authCode.getId());

		if (pollingUser == null) {
			Poller poller = new GmailPoller(gmailRepository, rabbitTemplate, authCode.getAuthCode(), authCode.getId());
			new Thread(poller).start();
		}
		else {
			if (authCode.getAuthCode() == null || authCode.getAuthCode().equals("")) {
				pollingUser.setRefreshToken(null);
				gmailRepository.save(pollingUser);
			}
			else
				new GmailPoller(gmailRepository, rabbitTemplate, authCode.getAuthCode(), authCode.getId()); // this will update the pollingUser in the repository.
		}
	}
}
