package PIM;

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
import data.*;
import com.unclutter.poller.*;

import java.util.concurrent.LinkedBlockingQueue;


@SpringBootApplication
public class Application {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	LoginController loginController;

	private final String topicResponseQueueName = "topic-response.frontend.rabbit";
	private final String userResponseQueueName = "user-registration-response.frontend.rabbit";
	private final String userCheckResponseQueueName = "user-check-response.frontend.rabbit";
	private final String itemResponseQueueName = "item-response.frontend.rabbit";
	private final String imageResponseQueueName = "image-response.frontend.rabbit";
	private final String settingsResponseQueueName = "user-update-response.frontend.rabbit";
	@Bean
	LinkedBlockingQueue<TopicResponse> topicResponseLL() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	LinkedBlockingQueue<ItemResponseIdentified> itemResponseLL() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	LinkedBlockingQueue<ImageResponseIdentified> imageResponseLL() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	LinkedBlockingQueue<UserIdentified> userResponseLL() {
		return new LinkedBlockingQueue<>();
	}
	
	@Bean
	LinkedBlockingQueue<UserIdentified> userCheckResponseLL() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	LinkedBlockingQueue<UserUpdateResponseIdentified> editUserSettingsResponseLL() {
		return new LinkedBlockingQueue<>();
	}


	@Bean
	Queue topicResponseQueue() {
		return new Queue(topicResponseQueueName, false);
	}

	@Bean
	Queue itemResponseQueue() {
		return new Queue(itemResponseQueueName, false);
	}

	@Bean
	Queue imageResponseQueue() {
		return new Queue(imageResponseQueueName, false);
	}

	@Bean
	Queue userResponseQueue() {
		return new Queue(userResponseQueueName, false);
	}

	@Bean
	Queue userCheckResponseQueue() {
		return new Queue(userCheckResponseQueueName, false);
	}
////////////////
	@Bean
	Queue editUserSettingsResponseQueue() {
		return new Queue(settingsResponseQueueName, false);
	}
//////////////////
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding topicResponseBinding(@Qualifier("topicResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicResponseQueueName);
	}

	@Bean
	Binding itemResponseBinding(@Qualifier("itemResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(itemResponseQueueName);
	}

	@Bean
	Binding imageResponseBinding(@Qualifier("imageResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(imageResponseQueueName);
	}

	@Bean
	Binding userResponseBinding(@Qualifier("userResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userResponseQueueName);
	}

	@Bean
	Binding userCheckResponseBinding(@Qualifier("userCheckResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userCheckResponseQueueName);
	}
/////////////Double check this.
	@Bean
	Binding editUserSettingsResponseBinding(@Qualifier("editUserSettingsResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(settingsResponseQueueName);
	}
///////////
	@Bean
	public FrontendListener frontendListener(RabbitTemplate rabbitTemplate) {
		return new FrontendListener(rabbitTemplate);
	}

	@Bean
	public MessageListenerAdapter topicResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveTopicResponse");
	}

	@Bean
	public MessageListenerAdapter itemResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveItemResponse");
	}

	@Bean
	public MessageListenerAdapter imageResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveImageResponse");
	}

	@Bean
	public MessageListenerAdapter userResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveUserRegistrationResponse");
	}

	@Bean
	public MessageListenerAdapter userCheckResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveUserCheckResponse");
	}
/////////////
	@Bean
	public MessageListenerAdapter editUserSettingsResponseAdapter(LoginController loginController) {
		return new MessageListenerAdapter(loginController, "receiveEditSettingsResponse");
	}

////////////
	@Bean
	public SimpleMessageListenerContainer topicResponseContainer(ConnectionFactory connectionFactory, @Qualifier("topicResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer itemResponseContainer(ConnectionFactory connectionFactory, @Qualifier("itemResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(itemResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}
	@Bean
	public SimpleMessageListenerContainer imageResponseContainer(ConnectionFactory connectionFactory, @Qualifier("imageResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(itemResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer userResponseContainer(ConnectionFactory connectionFactory, @Qualifier("userResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}
	@Bean
	public SimpleMessageListenerContainer userCheckResponseContainer(ConnectionFactory connectionFactory, @Qualifier("userCheckResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userCheckResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}
/////////////////
	@Bean
	public SimpleMessageListenerContainer editUserSettingsResponseContainer(ConnectionFactory connectionFactory, @Qualifier("editUserSettingsResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(settingsResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

////////////////////
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
