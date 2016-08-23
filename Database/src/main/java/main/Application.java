package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Component;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.*;
import listeners.*;
import data.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SpringBootApplication
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
	private final static String priorityProcessedDataQueueName = "priority-processed-data.database.rabbit";
	private final static String topicRequestQueueName = "topic-request.database.rabbit";
	private final String userRegisterQueueName = "user-register.database.rabbit";
	private final String userCheckQueueName = "user-check.database.rabbit";
	private final String userUpdateQueueName = "user-update.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	Queue topicRequestQueue() {
		return new Queue(topicRequestQueueName, false);
	}

	@Bean
	Queue processedDataQueue() {
		return new Queue(processedDataQueueName, false);
	}

	@Bean
	Queue priorityProcessedDataQueue() {
		return new Queue(priorityProcessedDataQueueName, false);
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
	Binding processedDataBinding(@Qualifier("processedDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(processedDataQueueName);
	}

	@Bean
	Binding priorityProcessedDataBinding(@Qualifier("processedDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(priorityProcessedDataQueueName);
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
	Binding userUpdateBinding(@Qualifier("userUpdateQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(userUpdateQueueName);
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
	public MessageListenerAdapter priorityProcessedDataAdapter(ProcessedDataListener processedDataListener) {
		return new MessageListenerAdapter(processedDataListener, "receivePriorityProcessedData");
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
	public MessageListenerAdapter userUpdateAdapter(BusinessListener businessListener) {
		return new MessageListenerAdapter(businessListener, "receiveUserUpdate");
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
	public SimpleMessageListenerContainer priorityProcessedDataContainer(ConnectionFactory connectionFactory, @Qualifier("priorityProcessedDataAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(priorityProcessedDataQueueName);
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

	@Bean
	public SimpleMessageListenerContainer userUpdateContainer(ConnectionFactory connectionFactory, @Qualifier("userUpdateAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(userUpdateQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		ctx.getEnvironment().setActiveProfiles("production");
	}

	@Override
	public void run(String... args) throws Exception {
		for (String arg : args) {
			switch (arg) {
				case "cleandb":
					System.out.println("Cleaning all databases...");
					userRepository.deleteAll();
					processedDataRepository.deleteAll();
					topicRepository.deleteAll();
					break;
				default:
					System.out.println("Invalid argument.");
					break;
			}
		}
	}
}
