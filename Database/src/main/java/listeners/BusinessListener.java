package listeners;

import data.*;
import repositories.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

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
	private final String userUpdateResponseQueueName = "user-update-response.frontend.rabbit";

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	public void receiveUserRegister(UserIdentified user) {
		boolean userAlreadyRegistered = true;
		User userReturn = userRepository.findByGmailId(user.getGmailId());

		if (userReturn == null) {
			userAlreadyRegistered = false;
			User saveUser = user.getUser(true);
			userRepository.save(saveUser);
			userReturn = userRepository.findByGmailId(saveUser.getGmailId());
		}
		else if (!userReturn.getIsActive()) {
			userReturn.setIsActive(true);
			userRepository.save(userReturn);
		}

		user = new UserIdentified(user.getReturnId(), userAlreadyRegistered, userReturn);
		System.out.println("Respond: " + user);
		rabbitTemplate.convertAndSend(userRegisterResponseQueueName, user);
	}

	public void receiveCheckIfRegistered(UserIdentified user) {
		User userReturn = userRepository.findByGmailId(user.getGmailId());

		if (userReturn == null || !userReturn.getIsActive())
			user.setIsRegistered(false);
		else
			user = new UserIdentified(user.getReturnId(), true, userReturn);

		System.out.println("Respond: " + user);
		rabbitTemplate.convertAndSend(userCheckResponseQueueName, user);
	}

	public void receiveUserUpdate(UserIdentified userIdentified) {
		System.out.println("Received: " + userIdentified);
		User userInRepo = userRepository.findByUserId(userIdentified.getUserId());
		UserUpdateResponseIdentified userUpdateResponseIdentified = new UserUpdateResponseIdentified(userIdentified.getReturnId());

		if (userInRepo == null)
			userUpdateResponseIdentified.setCode(UserUpdateResponse.USER_NOT_FOUND);
		else {
			if (userIdentified.getGmailId() != null)
				if (userIdentified.getGmailId().equals(""))
					userInRepo.setGmailId(null);
				else
					userInRepo.setGmailId(userIdentified.getGmailId());

			if (userIdentified.getTheme() != null)
				userInRepo.setTheme(userIdentified.getTheme());

			if (userIdentified.getInitialDepth() != userInRepo.getInitialDepth() && userIdentified.getInitialDepth() != -1)
				userInRepo.setInitialDepth(userIdentified.getInitialDepth());

			if (userIdentified.getBranchingFactor() != userInRepo.getBranchingFactor() && userIdentified.getBranchingFactor() != -1)
				userInRepo.setBranchingFactor(userIdentified.getBranchingFactor());

			if (userIdentified.getIsActive() != userInRepo.getIsActive())
				userInRepo.setIsActive(userIdentified.getIsActive());

			userRepository.save(userInRepo);
			userUpdateResponseIdentified.setCode(UserUpdateResponse.SUCCESS);
		}

		System.out.println("Respond: " + userUpdateResponseIdentified);
		rabbitTemplate.convertAndSend(userUpdateResponseQueueName, userUpdateResponseIdentified);
	}
}
