package com.unclutter.poller;

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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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

	private final String authCodeQueueName;
	private final String itemRequestQueueName;
	private ConfigurableListableBeanFactory beanFactory;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ConnectionFactory connectionFactory;

	private Queue queue(String queueName) {
		return new Queue(queueName, false);
	}

	private TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	private Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(itemRequestQueueName);
	}

	private MessageListenerAdapter adapter(Object listener, String methodName) {
		return new MessageListenerAdapter(listener, methodName);
	}

	private SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, String queueName) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	/**
	* Default constructor.
	*/
	public PollerFactory() {

	}

	/*
	* Constructor that sets up all the beans.
	*/
	public PollerFactory(String pollerName, PollingConfiguration pollerConfig, ConfigurableListableBeanFactory beanFactory) throws BeansNotSetUpException {
		setUpBeans(pollerName, pollerConfig, beanFactory);
	}

	/**
	* Manually set up the RabbitTemplate bean.
	*/
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	* Manually set up the ConnectionFactory bean.
	*/
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	/**
	* Set up all the beans that handle the RabbitMQ message queues.
	*/
	public void setUpBeans(String pollerName, PollingConfiguration pollerConfig, ConfigurableListableBeanFactory beanFactory) throws BeansNotSetUpException {
		if (connectionFactory == null)
			throw new BeansNotSetUpException("Either manually set up the beans or let this instance be set up as a bean.");

		this.beanFactory = beanFactory;
		String authCodeQueueName = "auth-code." + pollerConfig.getPollerName() + ".rabbit";
		String itemQueueName = "item-request." + pollerConfig.getPollerName() + ".rabbit";
		Queue authCodeQueue = queue(authCodeQueueName);
		Queue itemQueue = queue(itemQueueName);
		TopicExchange topicExchange = exchange();
		Binding authCodeBinding = binding(authCodeQueue, topicExchange);
		Binding itemBinding = binding(itemQueue, topicExchange);
		MessageListenerAdapter authCodeAdapter = adapter(pollerConfig.getAuthCodeListener(), pollerConfig.getAuthCodeMethod());
		MessageListenerAdapter itemAdapter = adapter(pollerConfig.getItemListener(), pollerConfig.getItemMethod());
		SimpleMessageListenerContainer authCodeContainer = container(connectionFactory, authCodeAdapter, authCodeQueueName);
		SimpleMessageListenerContainer itemContainer = container(connectionFactory, itemAdapter, itemQueueName);

		beanFactory.registerSingleton("authCodeContainer", authCodeContainer);
		beanFactory.registerSingleton("itemRequestContainer", itemContainer);
	}

	/**
	* Get a new instance of a message broker used for sending messages to other services.
	*/
	public MessageBroker getMessageBroker() {
		if (rabbitTemplate == null)
			throw new BeansNotSetUpException("Either manually set up the beans or let this instance be set up as a bean.");
	}
}
