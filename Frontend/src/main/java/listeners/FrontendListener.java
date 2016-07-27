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
}
