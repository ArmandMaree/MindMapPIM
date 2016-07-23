package listeners.frontend;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import data.*;

/**
* Waits for messages from the frontend and processes them.
*
* @author  Armand Maree
* @since   2016-07-20
*/
public class FrontendListener {
	private RabbitTemplate rabbitTemplate;
	private final String databaseRequestQueueName = "topic-request.database.rabbit";
	
	/**
	* Default FrontendListener constructor
	* @param rabbitTemplate Refernece to a rabbitTemplate to send messages to RabbitMQ.
	*/
	public FrontendListener(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	* Receives a request for new topics.
	* @param request structured as a topicRequest.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) {
		rabbitTemplate.convertAndSend(databaseRequestQueueName, topicRequest);
	}


}
