package listeners;

import data.AuthCode;
import data.TopicRequest;
import data.TopicResponse;
import data.User;
import data.UserIdentified;
import data.UserRegistrationIdentified;
import data.UserUpdateRequestIdentified;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the frontend and processes/forwards them.
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
	* Default constructor.
	*/
	public FrontendListener() {

	}

	/**
	* Receives a request for new topics.
	* <p>
	*	This method simply forwards the request to the database service.
	* </p>
	* @param topicRequest Request for topics structured as a TopicRequest.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) {
		System.out.println("Received: " + topicRequest);
		rabbitTemplate.convertAndSend(topicRequestQueueName, topicRequest);
	}

	/**
	* Receives a request for new topics.
	* <p>
	*	This method simply forwards the request to the database service.
	* </p>
	* @param topicResponse Response to a topic request structured as a TopicResponse.
	*/
	public void receiveTopicResponse(TopicResponse topicResponse) {
		System.out.println("Received: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
	}

	/**
	* Receives a request to register a user. It will also start any pollers that is specified.
	* <p>
	*	The name of the pim contained in each AuthCode object in the UserRegistrationIdentified parameter object is important.<br>
	*	The name MUST be all lowercase and the name must be the same as the name of the poller as specified when the poller is created. This is because the name of the PIM is used to determine to which RabbitMQ queue the AuthCodes should be sent to. Eg: if an AuthCode for Google's Mail Service (Gmail) is sent, then the name of the PIM should be "gmail", the AuthCode will thus be sent to "auth-code.gmail.rabbit". The poller's name will then also be set as "gmail" and it will wait on "auth-code.gmail.rabbit" for new AuthCodes.
	* </p>
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
	*	The name of the pim contained in each AuthCode object in the UserRegistrationIdentified parameter object is important.<br>
	*	The name MUST be all lowercase and the name must be the same as the name of the poller as specified when the poller is created. This is because the name of the PIM is used to determine to which RabbitMQ queue the AuthCodes should be sent to. Eg: if an AuthCode for Google's Mail Service (Gmail) is sent, then the name of the PIM should be "gmail", the AuthCode will thus be sent to "auth-code.gmail.rabbit". The poller's name will then also be set as "gmail" and it will wait on "auth-code.gmail.rabbit" for new AuthCodes.<br>
	*	<ul>
	*		<li>Any fields that does not need to be updated should be null (or -1 for integers).</li>
	*		<li>If you need to stop a poller, attach a AuthCode for that poller and make the authCode field null (or prepend the usserId with "stop:" if the specific PIM does not support the OAUTH protocol).</li>
	*		<li>To change the account a user is being polled for, attach an AuthCode that had a null authCode field for the old userId and another AuthCode that contains the details for the new poller, in that specific order.</li>
	*	</ul>
	* </p>
	* @param userUpdateIdentified Contains all the information needed to update a user.
	*/
	public void receiveUserUpdate(UserUpdateRequestIdentified userUpdateIdentified) {
		try {
			System.out.println("Received: " + userUpdateIdentified);
			UserIdentified user = new UserIdentified(userUpdateIdentified.getReturnId(), false, (User)userUpdateIdentified);
			user.setPersistMap(userUpdateIdentified.getPersistMap());

			if (userUpdateIdentified.getAuthCodes() != null) {
				for (AuthCode authCode : userUpdateIdentified.getAuthCodes()) {
					if (authCode.getAuthCode() != null && !authCode.getAuthCode().equals(""))
						user.addPimId(authCode.getPimSource(), authCode.getId());

					rabbitTemplate.convertAndSend("auth-code." + authCode.getPimSource() + ".rabbit", authCode);
					System.out.println("Sent to " + authCode.getPimSource() + " poller: " + authCode);

					if (authCode.getAuthCode() == null || authCode.getAuthCode().equals(""))
						user.addPimId(authCode.getPimSource(), "");
					else
						user.addPimId(authCode.getPimSource(), authCode.getId());
				}
			}

			System.out.println("Send to DB: " + user);
			rabbitTemplate.convertAndSend(userUpdateQueueName, user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
