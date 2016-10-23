package main;

import listeners.FrontendListener;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Bean;

/**
* Main Spring Boot application that runs and creates all the bean.
*
* @author  Armand Maree
* @since   1.0.0
*/
@SpringBootApplication
public class Application implements CommandLineRunner {
	private final String topicRequestQueueName = "topic-request.business.rabbit";
	private final String topicResponseQueueName = "topic-response.business.rabbit";
	private final String registerQueueName = "register.business.rabbit";
	private final String userUpdateQueueName = "user-update-request.business.rabbit";

	@Autowired
	public RabbitTemplate rabbitTemplate;

	@Bean
	public Queue topicRequestQueue() {
		return new Queue(topicRequestQueueName, false);
	}

	@Bean
	public Queue topicResponseQueue() {
		return new Queue(topicResponseQueueName, false);
	}

	@Bean
	public Queue registerQueue() {
		return new Queue(registerQueueName, false);
	}

	@Bean
	public Queue userUpdateQueue() {
		return new Queue(userUpdateQueueName, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	public Binding topicRequestBinding(@Qualifier("topicRequestQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicRequestQueueName);
	}

	@Bean
	public Binding topicResponseBinding(@Qualifier("topicResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicResponseQueueName);
	}

	@Bean
	public Binding registerBinding(@Qualifier("registerQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(registerQueueName);
	}

	@Bean
	public Binding userUpdateBinding(@Qualifier("userUpdateQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userUpdateQueueName);
	}

	@Bean
	public FrontendListener frontendListener() {
		return new FrontendListener();
	}

	@Bean
	public MessageListenerAdapter topicRequestAdapter(FrontendListener frontendListener) {
		return new MessageListenerAdapter(frontendListener, "receiveTopicRequest");
	}

	@Bean
	public MessageListenerAdapter topicResponseAdapter(FrontendListener frontendListener) {
		return new MessageListenerAdapter(frontendListener, "receiveTopicResponse");
	}

	@Bean
	public MessageListenerAdapter registerAdapter(FrontendListener frontendListener) {
		return new MessageListenerAdapter(frontendListener, "receiveRegister");
	}

	@Bean
	public MessageListenerAdapter userUpdateAdapter(FrontendListener frontendListener) {
		return new MessageListenerAdapter(frontendListener, "receiveUserUpdate");
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
	public SimpleMessageListenerContainer topicResponseContainer(ConnectionFactory connectionFactory, @Qualifier("topicResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer registerContainer(ConnectionFactory connectionFactory, @Qualifier("registerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(registerQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer userUpdateContainer(ConnectionFactory connectionFactory, @Qualifier("userUpdateAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userUpdateQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
