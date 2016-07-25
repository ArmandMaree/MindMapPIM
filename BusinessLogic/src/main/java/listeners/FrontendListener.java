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
		rabbitTemplate.convertAndSend(topicRequestQueueName, topicRequest);
	}

	/**
	* Receives a request for new topics.
	* @param topicResponse Response to a topic request structured as a TopicResponse.
	*/
	public void receiveTopicResponse(TopicResponse topicResponse) {
		System.out.println("Business received: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
	}
}
