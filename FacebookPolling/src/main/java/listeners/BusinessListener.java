package listeners;

import data.*;
import poller.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class BusinessListener {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	* Receives an AuthCode and starts a poller with the authCode contained in that class.
	* @param authCode The AuthCode that contains all the information needed to start the poller.
	*/
	public void receiveAuthCode(AuthCode authCode) {
		System.out.println("Starting for: " + authCode);
		FacebookPoller poller = new FacebookPoller(rabbitTemplate, authCode.getAuthCode(), authCode.getExpireTime(), authCode.getId());
		new Thread(poller).start();
	}
}
