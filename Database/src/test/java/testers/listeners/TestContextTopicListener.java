package testers.listeners;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import repositories.*;
import data.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class TestContextTopicListener  {
	// test beans start
	private final String topicResponseQueueName = "topic-response.business.rabbit";
	public final static String processedDataQueueName = "processed-data.database.rabbit";

	@Autowired
	private LinkedBlockingQueue<TopicResponse> topicResponseLinkedQueue;

	@Bean
	LinkedBlockingQueue<TopicResponse> testTopicResponseQueueDev() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue topicResponseQueue() {
		return new Queue(topicResponseQueueName, false);
	}

	@Bean
	Binding topicResponseBinding(@Qualifier("topicResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicResponseQueueName);
	}

	@Bean
	public MessageListenerAdapter topicResponseAdapter() {
		return new MessageListenerAdapter(this, "receiveTopicResponseResponse");
	}

	@Bean
	public SimpleMessageListenerContainer topicResponseContainer(ConnectionFactory connectionFactory, @Qualifier("topicResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveTopicResponseResponse(TopicResponse topicResponse) throws InterruptedException {
		System.out.println("Test Context Received: " + topicResponse);
		topicResponseLinkedQueue.put(topicResponse);
	}
	// test beans end
}
