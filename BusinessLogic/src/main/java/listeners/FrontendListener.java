package listeners;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;

/**
* Waits for messages from the frontend and processes them.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class FrontendListener {
	private final String topicRequestQueueName = "topic-request.database.rabbit";
	private final String topicResponseQueueName = "topic-response.frontend.rabbit";
	private final String userRegisterDatabaseQueueName = "user-register.database.rabbit";
	private final String authCodeQueueName = "auth-code.gmail.rabbit";
	private final String userUpdateQueueName = "user-update-request.database.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	* Default constructor
	*/
	public FrontendListener() {

	}

	/**
	* Receives a request for new topics.
	* @param topicRequest Request for topics structured as a TopicRequest.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) {
		System.out.println("Received: " + topicRequest);
		rabbitTemplate.convertAndSend(topicRequestQueueName, topicRequest);
	}

	/**
	* Receives a request for new topics.
	* @param topicResponse Response to a topic request structured as a TopicResponse.
	*/
	public void receiveTopicResponse(TopicResponse topicResponse) {
		System.out.println("Received: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
	}

	public void receiveRegister(UserRegistrationIdentified userRegistrationIdentified) {
		System.out.println("Received: " + userRegistrationIdentified);
		if (userRegistrationIdentified == null || userRegistrationIdentified.getAuthCodes() == null)
			return;

		UserIdentified user = new UserIdentified(userRegistrationIdentified.getReturnId(), false, userRegistrationIdentified.getFirstName(), userRegistrationIdentified.getLastName(), null);

		for (AuthCode authCode : userRegistrationIdentified.getAuthCodes()) {
			switch (authCode.getPimSource()) {
				case "Gmail":
					user.setGmailId(authCode.getId());
					rabbitTemplate.convertAndSend(authCodeQueueName, authCode);
					break;
				default:
					System.out.println("Unknown PIM Source: " + authCode.getPimSource());
					break;
			}
		}

		rabbitTemplate.convertAndSend(userRegisterDatabaseQueueName, user);
	}

	public void receiveUserUpdate(UserUpdateRequestIdentified userUpdateIdentified) {
		UserIdentified user = new UserIdentified(userUpdateIdentified.getReturnId(), false, (User)userUpdateIdentified);

		if (userUpdateIdentified.getAuthCodes() != null) {
			for (AuthCode authCode : userUpdateIdentified.getAuthCodes()) {
				switch (authCode.getPimSource()) {
					case "Gmail":
						user.setGmailId(authCode.getId());
						rabbitTemplate.convertAndSend(authCodeQueueName, authCode);
						break;
					default:
						System.out.println("Unknown PIM Source: " + authCode.getPimSource());
						break;
				}
			}
		}

		rabbitTemplate.convertAndSend(userUpdateQueueName, user);
	}
}
