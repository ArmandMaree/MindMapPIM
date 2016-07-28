package listeners;

import data.*;
import repositories.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class BusinessListener {
	private final String userResponseQueueName = "user-response.frontend.rabbit";

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	public void receiveUserRegister(UserIdentified user) {
		User saveUser = new User(user.getFirstName(), user.getLastName(), user.getGmailId());
		userRepository.save(saveUser);
		User userReturn = userRepository.findByGmailId(saveUser.getGmailId());
		user = new UserIdentified(user.getReturnId(), userReturn);
		rabbitTemplate.convertAndSend(userResponseQueueName, user);;
	}
}
