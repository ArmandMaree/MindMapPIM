package main;

import listeners.ImageListener;
import listeners.ProcessedDataListener;
import listeners.TopicListener;
import listeners.UserListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.PimProcessedDataRepository;
import repositories.TopicRepository;
import repositories.UserRepository;

/**
* Main Spring Boot application that runs and creates all the bean.
*
* @author  Armand Maree
* @since   1.0.0
*/
@SpringBootApplication
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
	private final static String priorityProcessedDataQueueName = "priority-processed-data.database.rabbit";
	private final static String topicRequestQueueName = "topic-request.database.rabbit";
	private final static String imageRequestQueueName = "image-request.database.rabbit";
	private final static String imageSaveQueueName = "image-save.database.rabbit";
	private final static String userRegisterQueueName = "user-register.database.rabbit";
	private final static String userCheckQueueName = "user-check.database.rabbit";
	private final static String userUpdateQueueName = "user-update-request.database.rabbit";
	private final static String topicUpdateQueueName = "topic-update-request.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Bean
	Queue imageRequestQueue() {
		return new Queue(imageRequestQueueName, false);
	}

	@Bean
	Queue imageSaveQueue() {
		return new Queue(imageRequestQueueName, false);
	}

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
	Queue topicUpdateQueue() {
		return new Queue(topicUpdateQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding imageRequestBinding(@Qualifier("imageRequestQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(imageRequestQueueName);
	}

	@Bean
	Binding imageSaveBinding(@Qualifier("imageSaveQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(imageSaveQueueName);
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
	Binding topicUpdateBinding(@Qualifier("topicUpdateQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(topicUpdateQueueName);
	}

	@Bean
	public ImageListener imageListener() {
		return new ImageListener();
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
	public UserListener userListener() {
		return new UserListener();
	}

	@Bean
	public MessageListenerAdapter imageRequestAdapter(ImageListener imageListener) {
		return new MessageListenerAdapter(imageListener, "receiveImageRequest");
	}

	@Bean
	public MessageListenerAdapter imageSaveAdapter(ImageListener imageListener) {
		return new MessageListenerAdapter(imageListener, "receiveImageSave");
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
	public MessageListenerAdapter userRegisterAdapter(UserListener userListener) {
		return new MessageListenerAdapter(userListener, "receiveUserRegister");
	}

	@Bean
	public MessageListenerAdapter userCheckAdapter(UserListener userListener) {
		return new MessageListenerAdapter(userListener, "receiveCheckIfRegistered");
	}

	@Bean
	public MessageListenerAdapter userUpdateAdapter(UserListener userListener) {
		return new MessageListenerAdapter(userListener, "receiveUserUpdate");
	}

	@Bean
	public MessageListenerAdapter topicUpdateAdapter(TopicListener topicListener) {
		return new MessageListenerAdapter(topicListener, "receiveTopicUpdate");
	}

	@Bean
	public SimpleMessageListenerContainer imageRequestContainer(ConnectionFactory connectionFactory, @Qualifier("imageRequestAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(imageRequestQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer imageSaveContainer(ConnectionFactory connectionFactory, @Qualifier("imageSaveAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(imageSaveQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
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

	@Bean
	public SimpleMessageListenerContainer topicUpdateContainer(ConnectionFactory connectionFactory, @Qualifier("topicUpdateAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(topicUpdateQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
		ctx.getEnvironment().setActiveProfiles("production");
	}

	/**
	* Runs the {@link org.springframework.boot.CommandLineRunner} program.
	* <p>
	*	The commandline parameters that are supported are:
	*	<ul>
	*		<li>cleandb - This will clean all the repositories.</li>
	*	</ul>
	* </p>
	*/
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
					System.out.println("Invalid argument: " + arg);
					break;
			}
		}
	}
}
