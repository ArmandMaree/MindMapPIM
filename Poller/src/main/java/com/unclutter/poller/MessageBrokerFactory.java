package com.unclutter.poller;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

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

	@Autowired
	private ConnectionFactory connectionFactory;

	private Queue queue(String queueName) {
		System.out.println("Creating Queue with name: " + queueName);
		return new Queue(queueName, false);
	}

	@Bean
	private TopicExchange exchange() {
		System.out.println("Creating TopicExchange.");
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	private Binding authCodeBinding(TopicExchange exchange) {
		String queueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";
		System.out.println("Creating bindig with queueName: " + queueName + " and TopicExchange: " + exchange);
		return BindingBuilder.bind(queue(queueName)).to(exchange).with(queueName);
	}

	@Bean
	private Binding itemRequestBinding(TopicExchange exchange) {
		String queueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";
		System.out.println("Creating bindig with queueName: " + queueName + " and TopicExchange: " + exchange);
		return BindingBuilder.bind(queue(queueName)).to(exchange).with(queueName);
	}

	private MessageListenerAdapter adapter(Object listener, String methodName) {
		System.out.println("Creating adapter with listener: " + listener + " and methodName: " + methodName);
		return new MessageListenerAdapter(listener, methodName);
	}

	@Bean
	private SimpleMessageListenerContainer authCodeContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		MessageListenerAdapter listenerAdapter = adapter(pollerConfig.getAuthCodeListener(), pollerConfig.getAuthCodeMethod());
		String queueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";

		System.out.println("Creating container with queueName: " + queueName + " and connectionFactory: " + connectionFactory + " and listenerAdapter: " + listenerAdapter);

		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	private SimpleMessageListenerContainer itemRequestContainer(ConnectionFactory connectionFactory) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		MessageListenerAdapter listenerAdapter = adapter(pollerConfig.getItemListener(), pollerConfig.getItemMethod());
		String queueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";

		System.out.println("Creating container with queueName: " + queueName + " and connectionFactory: " + connectionFactory + " and listenerAdapter: " + listenerAdapter);

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
