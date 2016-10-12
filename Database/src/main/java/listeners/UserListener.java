package listeners;

import data.PimId;
import data.User;
import data.UserIdentified;
import data.UserUpdateResponse;
import data.UserUpdateResponseIdentified;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import repositories.PimProcessedDataRepository;
import repositories.TopicRepository;
import repositories.UserRepository;

/**
* This class waits for messages that require operations on user information.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserListener {
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
	*		<li>If the user is already registered then a {@link data.User} will be returned that contains all the information that is in the database except the isRegistered member variable will be set to true.</li>
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

	/**
	* Checks whether a user is registered.
	* <p>
	*	Checks if any of the provided PimIds match any of the users in the database.
	* </p>
	* @param user The user that must be checked.
	*/
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

	/**
	* Updates the information of a user in the database.
	* <p>
	*	If the {@link data.PimId}s of a user has to be updated:
	*	<ul>
	*		<li>If a {@link data.PimId} has to be removed then make the {@link data.PimId#uId} empty.</li>
	*		<li>If a {@link data.PimId} has to be changed then make the {@link data.PimId#uId} the new ID.</li>
	*	</ul>
	* </p>
	*
	*/
	public void receiveUserUpdate(UserIdentified userIdentified) {
		System.out.println("Receive in update: " + userIdentified);
		User userInRepo = userRepository.findByUserId(userIdentified.getUserId());
		UserUpdateResponseIdentified userUpdateResponseIdentified = new UserUpdateResponseIdentified(userIdentified.getReturnId());

		if (userInRepo == null)
			userUpdateResponseIdentified.setCode(UserUpdateResponse.USER_NOT_FOUND);
		else {
			if (userIdentified.getPimIds() != null)
				for (PimId pimId : userIdentified.getPimIds())
					if (pimId.uId.equals("")) // remove pimId from user
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
