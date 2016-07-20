package listeners.frontend;

/**
* Waits for messages from the frontend and processes them.
*
* @author  Armand Maree
* @since   2016-07-20
*/
public class FrontendListener {
	private RabbitTemplate rabbitTemplate;

	/**
	* Receives a request for new topics.
	* @param request structured as a topicRequest.
	*/
	public void topicListener(TopicRequest topicRequest) {
		System.out.println(topicRequest);
	}

	/**
	* Default FrontendListener constructor
	* @param rabbitTemplate Refernece to a rabbitTemplate to send messages to RabbitMQ.
	*/
	public FrontendListener(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
}
