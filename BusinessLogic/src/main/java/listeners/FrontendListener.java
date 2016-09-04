package listeners;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;

/**
* Waits for messages from the frontend and processes them.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FrontendListener {
	private final String topicRequestQueueName = "topic-request.database.rabbit";
	private final String topicResponseQueueName = "topic-response.frontend.rabbit";
	private final String userRegisterDatabaseQueueName = "user-register.database.rabbit";
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

	/**
	* Receives a request to register a user. It will also start any pollers that is specified.
	* @param userRegistrationIdentified Contains all the information required to register a user.
	*/
	public void receiveRegister(UserRegistrationIdentified userRegistrationIdentified) {
		System.out.println("Received: " + userRegistrationIdentified);
		if (userRegistrationIdentified == null || userRegistrationIdentified.getAuthCodes() == null)
			return;

		UserIdentified user = new UserIdentified(userRegistrationIdentified.getReturnId(), false, userRegistrationIdentified.getFirstName(), userRegistrationIdentified.getLastName());

		for (AuthCode authCode : userRegistrationIdentified.getAuthCodes()) {
			user.addPimId(authCode.getPimSource(), authCode.getId());
			rabbitTemplate.convertAndSend("auth-code." + authCode.getPimSource() + ".rabbit", authCode);
		}

		rabbitTemplate.convertAndSend(userRegisterDatabaseQueueName, user);
	}

	/**
	* Receives a request to update a previously saved user. It will also stop/stop any pollers that are specified.
	* <p>
	*	<ul>
	*		<li>Any fields that does not need to be updated should be null (or -1 for integers).</li>
	*		<li>If you need to stop a poller, attach a AuthCode for that poller and make the authCode field null.</li>
	*		<li>To change the account a user is being polled for, attach an AuthCode that had a null authCode field for the old userId and another AuthCode that contains the details for the new  poller.</li>
	*	</ul>
	* </p>
	* @param userUpdateIdentified Contains all the information needed to update a user.
	*/
	public void receiveUserUpdate(UserUpdateRequestIdentified userUpdateIdentified) {
		System.out.println("Received: " + userUpdateIdentified);
		UserIdentified user = new UserIdentified(userUpdateIdentified.getReturnId(), false, (User)userUpdateIdentified);

		if (userUpdateIdentified.getAuthCodes() != null) {
			for (AuthCode authCode : userUpdateIdentified.getAuthCodes()) {
				if (authCode.getAuthCode() != null && !authCode.getAuthCode().equals(""))
					user.addPimId(authCode.getPimSource(), authCode.getId());

				rabbitTemplate.convertAndSend("auth-code." + authCode.getPimSource() + ".rabbit", authCode);
				System.out.println("Sent to " + authCode.getAuthCode() + " poller: " + authCode);
			}
		}

		System.out.println("Send to DB: " + user);
		rabbitTemplate.convertAndSend(userUpdateQueueName, user);
	}
}
