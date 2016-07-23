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
import listeners.*;
import data.*;

@SpringBootApplication
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	final static String processedDataQueueName = "processed-data.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue processedDataQueue() {
		return new Queue(processedDataQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding processedDataBinding(@Qualifier("processedDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(processedDataQueueName);
	}

	@Bean
	SimpleMessageListenerContainer processedDataContainer(ConnectionFactory connectionFactory, @Qualifier("processedDataListenerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(processedDataQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

    @Bean
    ProcessedDataListener processedDataListener(PimProcessedDataRepository processedDataRepository, UserRepository userRepository) {
        return new ProcessedDataListener(processedDataRepository, userRepository);
    }

	@Bean
	MessageListenerAdapter processedDataListenerAdapter(ProcessedDataListener processedDataListener) {
		return new MessageListenerAdapter(processedDataListener, "receiveProcessedData");
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.deleteAll();
		processedDataRepository.deleteAll();

		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		userRepository.save(acuben);
	}
}
