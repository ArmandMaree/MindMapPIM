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

import listeners.*;
import data.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class TestContext {
	public final static String topicRequestBusinessQueueName = "topic-request.business.rabbit";
	public final static String topicResponseBusinessQueueName = "topic-response.business.rabbit";
	public final static String registerBusinessQueueName = "register.business.rabbit";
	public final static String userUpdateDatabaseQueueName = "user-update-request.database.rabbit";
	public final static String userUpdateBusinessQueueName = "user-update-request.business.rabbit";

	// test beans start
	private final String userRegisterDatabaseQueueName = "user-register.database.rabbit";
	private final String authCodeGmailQueueName = "auth-code.gmail.rabbit";

	@Autowired
	@Qualifier("testUserQueueDev")
	private LinkedBlockingQueue<UserIdentified> userIdentifiedQueue;

	@Bean
	LinkedBlockingQueue<UserIdentified> testUserQueueDev() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue userRegisterDatabaseQueue() {
		return new Queue(userRegisterDatabaseQueueName, false);
	}

	@Bean
	Queue userUpdateDatabaseQueue() {
		return new Queue(userUpdateDatabaseQueueName, false);
	}

	@Bean
	Binding userRegisterDatabaseBinding(@Qualifier("userRegisterDatabaseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userRegisterDatabaseQueueName);
	}

	@Bean
	Binding userUpdateDatabaseBinding(@Qualifier("userUpdateDatabaseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userUpdateDatabaseQueueName);
	}
	
	@Bean
	public MessageListenerAdapter userIdentifedDatabaseAdapter() {
		return new MessageListenerAdapter(this, "receiveUserIdentifiedResponse");
	}

	@Bean
	public SimpleMessageListenerContainer userRegisterDatabaseContainer(ConnectionFactory connectionFactory, @Qualifier("userIdentifedDatabaseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userRegisterDatabaseQueueName, userUpdateDatabaseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveUserIdentifiedResponse(UserIdentified userIdentified) throws InterruptedException {
		System.out.println("Test Context Received: " + userIdentified);
		userIdentifiedQueue.put(userIdentified);
	}

	// auth code
	@Autowired
	@Qualifier("testAuthCodeQueueDev")
	private LinkedBlockingQueue<AuthCode> authCodeQueue;

	@Bean
	LinkedBlockingQueue<AuthCode> testAuthCodeQueueDev() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue authCodeGmailQueue() {
		return new Queue(authCodeGmailQueueName, false);
	}

	@Bean
	Binding authCodeGmailBinding(@Qualifier("authCodeGmailQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(authCodeGmailQueueName);
	}

	@Bean
	public MessageListenerAdapter authCodeGmailAdapter() {
		return new MessageListenerAdapter(this, "receiveAuthCodeGmailResponse");
	}

	@Bean
	public SimpleMessageListenerContainer authCodeGmailContainer(ConnectionFactory connectionFactory, @Qualifier("authCodeGmailAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(authCodeGmailQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveAuthCodeGmailResponse(AuthCode authCode) throws InterruptedException {
		System.out.println("Test Context Received: " + authCode);
		authCodeQueue.put(authCode);
	}
	// test beans end
}
