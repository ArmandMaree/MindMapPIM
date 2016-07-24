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
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.user.*;
import repositories.pimprocesseddata.*;
import repositories.topic.*;
import listeners.*;
import data.*;

import java.util.List;
import java.util.ArrayList;

@SpringBootApplication
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
	private final static String topicRequestQueueName = "topic-request.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue topicRequestQueue() {
		return new Queue(topicRequestQueueName, false);
	}

	@Bean
	Queue processedDataQueue() {
		return new Queue(processedDataQueueName, false);
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
		return BindingBuilder.bind(queue).to(exchange).with(topicRequestQueueName);
	}

	@Bean
	public TopicListener topicListener(RabbitTemplate rabbitTemplate, UserRepository userRepository, PimProcessedDataRepository processedDataRepository, TopicRepository topicRepository) {
		return new TopicListener(rabbitTemplate, userRepository, processedDataRepository, topicRepository);
	}

	@Bean
	public ProcessedDataListener processedDataListener(UserRepository userRepository, PimProcessedDataRepository processedDataRepository, TopicRepository topicRepository) {
		return new ProcessedDataListener(userRepository, processedDataRepository, topicRepository);
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

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.deleteAll();
		processedDataRepository.deleteAll();
		topicRepository.deleteAll();

		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		userRepository.save(acuben);
	}
}
