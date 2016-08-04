package listeners;

import data.*;
import repositories.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.LinkedBlockingQueue;

/**
* Waits for messages from the business service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class BusinessListener {
	private final String userRegisterResponseQueueName = "user-registration-response.frontend.rabbit";
	private final String userCheckResponseQueueName = "user-check-response.frontend.rabbit";

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private LinkedBlockingQueue<UserIdentified> testQueue;

	public void receiveUserRegister(UserIdentified user) {
		boolean userAlreadyRegistered = true;
		User userReturn = userRepository.findByGmailId(user.getUser(true).getGmailId());

		if (userReturn == null) {
			userAlreadyRegistered = false;
			User saveUser = user.getUser(true);
			userRepository.save(saveUser);
			userReturn = userRepository.findByGmailId(saveUser.getGmailId());
		}

		user = new UserIdentified(user.getReturnId(), userAlreadyRegistered, userReturn);
		rabbitTemplate.convertAndSend(userRegisterResponseQueueName, user);
		System.out.println("Database responded to register: " + user);

		try {
			testQueue.put(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receiveCheckIfRegistered(UserIdentified user) {
		User userReturn = userRepository.findByGmailId(user.getUser(true).getGmailId());
		user.setIsRegistered(userReturn != null);
		rabbitTemplate.convertAndSend(userCheckResponseQueueName, user);

		try {
			testQueue.put(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
