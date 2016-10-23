package com.unclutter.poller;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Component;

/**
* A factory that creates all the beans necessary to interact with RabbitMQ and produces objects (MessageBroker) that does this interaction. This class should be managed by Spring and must thus be created as a bean.
*
* @author  Armand Maree
* @since   1.0.0
*/
@Component
public class MessageBrokerFactory {
	public class BeansNotSetUpException extends Exception {
		public BeansNotSetUpException() {
			super();
		}

		public BeansNotSetUpException(String message) {
			super(message);
		}

		public BeansNotSetUpException(String message, Throwable cause) {
			super(message, cause);
		}

		public BeansNotSetUpException(Throwable cause) {
			super(cause);
		}

	}

	private PollingConfiguration pollerConfig;

	@Autowired
	public RabbitTemplate rabbitTemplate;

	@Bean
	public MessageBroker messageBroker(RabbitTemplate rabbitTemplate) {
		return new MessageBroker(rabbitTemplate);
	}

	@Bean
	private TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	private Queue authCodeQueue() {
		String queueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";
		return new Queue(queueName, false);
	}

	@Bean
	private Binding authCodeBinding(@Qualifier("authCodeQueue") Queue queue, TopicExchange exchange) {
		String queueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	private MessageListenerAdapter authCodeAdapter() {
		System.out.println("Creating authCodeAdapter for: " + pollerConfig.getAuthCodeListener() + "   " + pollerConfig.getAuthCodeMethod());
		return new MessageListenerAdapter(pollerConfig.getAuthCodeListener(), pollerConfig.getAuthCodeMethod());
	}

	@Bean
	private SimpleMessageListenerContainer authCodeContainer(ConnectionFactory connectionFactory, @Qualifier("authCodeAdapter") MessageListenerAdapter listenerAdapter) {
		String queueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";
		System.out.println("Creating authCodeContainer for: " + queueName);
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	private MessageListenerAdapter itemRequestAdapter() {
		return new MessageListenerAdapter(pollerConfig.getItemRequestListener(), pollerConfig.getItemRequestMethod());
	}

	@Bean
	private Queue itemRequestQueue() {
		String queueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";
		return new Queue(queueName, false);
	}

	@Bean
	private Binding itemRequestBinding(@Qualifier("itemRequestQueue") Queue queue, TopicExchange exchange) {
		String queueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	private SimpleMessageListenerContainer itemRequestContainer(ConnectionFactory connectionFactory, @Qualifier("itemRequestAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		String queueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	/**
	* Constructor that sets up all the beans.
	* @param pollerConfig Contains all the information needed to ceate the beans.
	*/
	public MessageBrokerFactory(PollingConfiguration pollerConfig) {
		this.pollerConfig = pollerConfig;
	}

	/**
	* Manually set up the RabbitTemplate if it couldn't be injected.
	* @param rabbitTemplate Used to send messages via RabbitMQ.
	*/
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	* Get a new instance of a message broker used for sending messages to other services.
	* @return An instance of a MessageBrokoer that should be used by the poller to send messages to the backend.
	*/
	public MessageBroker getMessageBroker() throws BeansNotSetUpException {
		if (rabbitTemplate == null)
			throw new BeansNotSetUpException("Either manually set up the beans or let this instance be set up as a bean.");

		return new MessageBroker(rabbitTemplate);
	}
}
