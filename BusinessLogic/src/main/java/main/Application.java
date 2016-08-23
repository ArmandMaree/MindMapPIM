package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Component;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import listeners.*;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private final String topicRequestQueueName = "topic-request.business.rabbit";
	private final String topicResponseQueueName = "topic-response.business.rabbit";
	private final String registerQueueName = "register.business.rabbit";
	private final String userUpdateQueueName = "user-update-request.business.rabbit";

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue topicRequestQueue() {
		return new Queue(topicRequestQueueName, false);
	}

	@Bean
	Queue topicResponseQueue() {
		return new Queue(topicResponseQueueName, false);
	}

	@Bean
	Queue registerQueue() {
		return new Queue(registerQueueName, false);
	}

	@Bean
	Queue userUpdateQueue() {
		return new Queue(userUpdateQueueName, false);
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
	Binding topicResponseBinding(@Qualifier("topicResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicResponseQueueName);
	}

	@Bean
	Binding registerBinding(@Qualifier("registerQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(registerQueueName);
	}

	@Bean
	Binding userUpdateBinding(@Qualifier("userUpdateQueue") Queue queue, TopicExchange exchange) {
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
