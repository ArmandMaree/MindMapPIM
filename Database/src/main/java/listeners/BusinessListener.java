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
* @since   1.0.0
*/
public class BusinessListener {
	private final String userRegisterResponseQueueName = "user-registration-response.frontend.rabbit";
	private final String userCheckResponseQueueName = "user-check-response.frontend.rabbit";
	private final String userUpdateResponseQueueName = "user-update-response.frontend.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	/**
	* Receives a request to register a user.
	* <p>
	*	<ul>
	*		<li>If the user is not registered yet then the user will be registered and a {@link data.User} will be returned that contains the same values as the object in the parameter, except the isRegistered member variable will be false and the userId filed will have the ID used in the database.</li>
	*		<li>If the user is already registered then a {@link data.User} will be returned that contains all the information that is in the database except the isRegistered member variable will be set tpo true.</li>
	*	</ul>
	* </p>
	* @param user Contains all the details of the user.
	*/
	public void receiveUserRegister(UserIdentified user) {
		System.out.println("Receive in register: " + user);
		boolean userAlreadyRegistered = true;
		User userReturn = null;

		for (PimId pimId : user.getPimIds()) {
			userReturn = userRepository.findByPimId(pimId.pim, pimId.uId);

			if (userReturn != null)
				break;
		}

		if (userReturn == null) {
			userAlreadyRegistered = false;
			User saveUser = user;
			userRepository.save(saveUser);

			for (PimId pimId : user.getPimIds()) {
				userReturn = userRepository.findByPimId(pimId.pim, pimId.uId);

				if (userReturn != null)
					break;
			}
		}
		else if (!userReturn.getIsActive()) {
			userReturn.setIsActive(true);
			userRepository.save(userReturn);
		}

		user = new UserIdentified(user.getReturnId(), userAlreadyRegistered, userReturn);
		System.out.println("Respond from register: " + user);
		rabbitTemplate.convertAndSend(userRegisterResponseQueueName, user);
	}

	public void receiveCheckIfRegistered(UserIdentified user) {
		System.out.println("Receive in check: " + user);
		User userReturn = null;

		for (PimId pimId : user.getPimIds()) {
			userReturn = userRepository.findByPimId(pimId.pim, pimId.uId);

			if (userReturn != null)
				break;
		}

		if (userReturn == null || !userReturn.getIsActive())
			user.setIsRegistered(false);
		else
			user = new UserIdentified(user.getReturnId(), true, userReturn);

		System.out.println("Respond from check: " + user);
		rabbitTemplate.convertAndSend(userCheckResponseQueueName, user);
	}

	public void receiveUserUpdate(UserIdentified userIdentified) {
		System.out.println("Receive in update: " + userIdentified);
		User userInRepo = userRepository.findByUserId(userIdentified.getUserId());
		UserUpdateResponseIdentified userUpdateResponseIdentified = new UserUpdateResponseIdentified(userIdentified.getReturnId());

		if (userInRepo == null)
			userUpdateResponseIdentified.setCode(UserUpdateResponse.USER_NOT_FOUND);
		else {
			if (userIdentified.getPimIds() != null)
				for (PimId pimId : userIdentified.getPimIds())
					if (pimId.uId.equals("") || pimId.uId.startsWith("stop:")) // remove pimId from user
						userInRepo.removePimId(pimId.uId);
					else // update pimID
						userInRepo.addPimId(pimId.pim, pimId.uId);

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
			System.out.println("USER AFTER MODIFICATION: " + userInRepo);
		}

		System.out.println("Respond from update: " + userUpdateResponseIdentified);
		rabbitTemplate.convertAndSend(userUpdateResponseQueueName, userUpdateResponseIdentified);
	}
}
