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
public class TestContext {
	public final static String topicRequestQueueName = "topic-request.database.rabbit";
	public final static String userRegisterQueueName = "user-register.database.rabbit";
	public final static String userCheckQueueName = "user-check.database.rabbit";

	// test beans start
	public final static String topicResponseQueueName = "topic-response.frontend.rabbit";
	public final static String userRegisterResponseQueueName = "user-registration-response.frontend.rabbit";
	public final static String userCheckResponseQueueName = "user-check-response.frontend.rabbit";
	public final static String topicResponseBusinessQueueName = "topic-response.business.rabbit";
	public final static String processedDataQueueName = "processed-data.database.rabbit";

	@Autowired
	private LinkedBlockingQueue<UserIdentified> queue;

	@Bean
	LinkedBlockingQueue<UserIdentified> testUserQueueDev() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue userRegistrationResponseQueue() {
		return new Queue(userRegisterResponseQueueName, false);
	}

	@Bean
	Queue userCheckResponseQueue() {
		return new Queue(userCheckResponseQueueName, false);
	}

	@Bean
	Binding userRegistrationResponseBinding(@Qualifier("userRegistrationResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userRegisterResponseQueueName);
	}

	@Bean
	Binding userCheckResponseBinding(@Qualifier("userCheckResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userCheckResponseQueueName);
	}

	@Bean
	public MessageListenerAdapter userRegistrationResponseAdapter() {
		return new MessageListenerAdapter(this, "receiveUserRegistrationResponse");
	}

	@Bean
	public SimpleMessageListenerContainer userRegistrationResponseContainer(ConnectionFactory connectionFactory, @Qualifier("userRegistrationResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userRegisterResponseQueueName, userCheckResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveUserRegistrationResponse(UserIdentified userIdentified) throws InterruptedException {
		System.out.println("Test Context Received: " + userIdentified);
		queue.put(userIdentified);
	}

	@Autowired
	private LinkedBlockingQueue<TopicResponse> topicResponseLinkedQueue;

	@Bean
	LinkedBlockingQueue<TopicResponse> testTopicResponseQueueDev() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue topicResponseQueue() {
		return new Queue(topicResponseBusinessQueueName, false);
	}

	@Bean
	Binding topicResponseBinding(@Qualifier("topicResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicResponseBusinessQueueName);
	}

	@Bean
	public MessageListenerAdapter topicResponseAdapter() {
		return new MessageListenerAdapter(this, "receiveTopicResponseResponse");
	}

	@Bean
	public SimpleMessageListenerContainer topicResponseContainer(ConnectionFactory connectionFactory, @Qualifier("topicResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicResponseBusinessQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveTopicResponseResponse(TopicResponse topicResponse) throws InterruptedException {
		System.out.println("Test Context Received: " + topicResponse);
		topicResponseLinkedQueue.put(topicResponse);
	}
	// test beans end
}
