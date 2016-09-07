package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

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

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;

import poller.*;
import listeners.*;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private final String authCodeQueueName = "auth-code.facebook.rabbit";
	private final String itemRequestQueueName = "item-request.facebook.rabbit";


	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	Queue authCodeQueue() {
		return new Queue(authCodeQueueName, false);
	}

	@Bean
	Queue itemRequestQueue() {
		return new Queue(itemRequestQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding authCodeBinding(@Qualifier("authCodeQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(authCodeQueueName);
	}

	@Bean
	Binding itemRequestBinding(@Qualifier("itemRequestQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(itemRequestQueueName);
	}

	@Bean
	public BusinessListener businessListener() {
		return new BusinessListener();
	}

	@Bean
	public FrontendListener frontendListener() {
		return new FrontendListener();
	}

	@Bean
	public MessageListenerAdapter authCodeAdapter(BusinessListener businessListener) {
		return new MessageListenerAdapter(businessListener, "receiveAuthCode");
	}

	@Bean
	public MessageListenerAdapter itemRequestAdapter(FrontendListener frontendListener) {
		return new MessageListenerAdapter(frontendListener, "receiveItemRequest");
	}

	@Bean
	public SimpleMessageListenerContainer authCodeContainer(ConnectionFactory connectionFactory, @Qualifier("authCodeAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(authCodeQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer itemRequestContainer(ConnectionFactory connectionFactory, @Qualifier("itemRequestAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(itemRequestQueueName);
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
