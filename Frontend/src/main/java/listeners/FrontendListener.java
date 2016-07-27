package listeners;

import java.util.List;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

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

	// /**
	// * Receives a request for new topics.
	// * @param request structured as a topicRequest.
	// */
	// public void receiveTopicRequest(TopicRequest topicRequest) {
	// 	rabbitTemplate.convertAndSend(databaseRequestQueueName, topicRequest);
	// }

	/**
	* Receives a request for new topics.
	* @param topicRequest structured as a topicRequest.
	*/
	public void receiveTopicResponse(TopicResponse topicResponse) {
		System.out.println("Frontend received: " + topicResponse);
		//this is where the topic response will be recieved containing the TopicResponse
		// process info here and send to front end through websocket
	}
}
