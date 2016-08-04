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
import listeners.*;
import data.*;

@Configuration
public class TestContext {
	public final static String processedDataQueueName = "processed-data.database.rabbit";
	public final static String topicRequestQueueName = "topic-request.database.rabbit";
	public final static String userRegisterQueueName = "user-register.database.rabbit";
	public final static String userCheckQueueName = "user-check.database.rabbit";

	// test beans start
	public final static String topicResponseQueueName = "topic-response.frontend.rabbit";
	public final static String userRegisterResponseQueueName = "user-registration-response.frontend.rabbit";
	public final static String userCheckResponseQueueName = "user-check-response.frontend.rabbit";

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
	// test beans end

	@Bean
	Queue topicRequestQueue() {
		return new Queue(topicRequestQueueName, false);
	}

	@Bean
	Queue processedDataQueue() {
		return new Queue(processedDataQueueName, false);
	}

	@Bean
	Queue userRegisterQueue() {
		return new Queue(userRegisterQueueName, false);
	}

	@Bean
	Queue userCheckQueue() {
		return new Queue(userCheckQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding topicRequestBinding(@Qualifier("topicRequestQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicRequestQueueName);
	}

	@Bean
	Binding processedDataBinding(@Qualifier("processedDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(processedDataQueueName);
	}

	@Bean
	Binding userRegisterBinding(@Qualifier("userRegisterQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userRegisterQueueName);
	}

	@Bean
	Binding userCheckBinding(@Qualifier("userCheckQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userCheckQueueName);
	}

	@Bean
	public TopicListener topicListener() {
		return new TopicListener();
	}

	@Bean
	public ProcessedDataListener processedDataListener() {
		return new ProcessedDataListener();
	}

	@Bean
	public BusinessListener businessListener() {
		return new BusinessListener();
	}

	@Bean
	public MessageListenerAdapter topicRequestAdapter(TopicListener topicListener) {
		return new MessageListenerAdapter(topicListener, "receiveTopicRequest");
	}

	@Bean
	public MessageListenerAdapter processedDataAdapter(ProcessedDataListener processedDataListener) {
		return new MessageListenerAdapter(processedDataListener, "receiveProcessedData");
	}

	@Bean
	public MessageListenerAdapter userRegisterAdapter(BusinessListener businessListener) {
		return new MessageListenerAdapter(businessListener, "receiveUserRegister");
	}

	@Bean
	public MessageListenerAdapter userCheckAdapter(BusinessListener businessListener) {
		return new MessageListenerAdapter(businessListener, "receiveCheckIfRegistered");
	}

	@Bean
	public SimpleMessageListenerContainer topicRequestContainer(ConnectionFactory connectionFactory, @Qualifier("topicRequestAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicRequestQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer processedDataContainer(ConnectionFactory connectionFactory, @Qualifier("processedDataAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(processedDataQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer userRegisterContainer(ConnectionFactory connectionFactory, @Qualifier("userRegisterAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userRegisterQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer userCheckContainer(ConnectionFactory connectionFactory, @Qualifier("userCheckAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userCheckQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}
}
