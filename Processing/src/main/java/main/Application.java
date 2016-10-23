package main;

import nlp.NaturalLanguageProcessor;
import nlp.StanfordNLP;

import listeners.ProcessingManager;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   1.0.0
*/
@SpringBootApplication
public class Application implements CommandLineRunner {
	final static String rawDataQueueName = "raw-data.processing.rabbit";
	final static String priorityRawDataQueueName = "priority-raw-data.processing.rabbit";

	@Autowired
	NaturalLanguageProcessor nlp;

	@Autowired
	ProcessingManager processingManager;

	@Autowired
	@Qualifier("processingManagerContainer")
	SimpleMessageListenerContainer processingManagerContainer;

	@Bean
	NaturalLanguageProcessor naturalLanguageProcessor() {
		return new StanfordNLP();
	}

	@Bean
	Queue rawDataQueue() {
		return new Queue(rawDataQueueName, false);
	}

	@Bean
	Queue priorityRawDataQueue() {
		return new Queue(priorityRawDataQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding rawDataBinding(@Qualifier("rawDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(rawDataQueueName);
	}

	@Bean
	Binding priorityRawDataBinding(@Qualifier("priorityRawDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(priorityRawDataQueueName);
	}

	@Bean
	ProcessingManager processingManager(NaturalLanguageProcessor naturalLanguageProcessor, RabbitTemplate rabbitTemplate) {
		return new ProcessingManager(naturalLanguageProcessor, rabbitTemplate);
	}

	@Bean
	MessageListenerAdapter processingManagerAdapter(ProcessingManager processingManager) {
		return new MessageListenerAdapter(processingManager, "receiveRawData");
	}

	@Bean
	MessageListenerAdapter priorityProcessingManagerAdapter(ProcessingManager processingManager) {
		return new MessageListenerAdapter(processingManager, "receivePriorityRawData");
	}

	@Bean
	SimpleMessageListenerContainer processingManagerContainer(ConnectionFactory connectionFactory, @Qualifier("processingManagerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(rawDataQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer priorityProcessingManagerContainer(ConnectionFactory connectionFactory, @Qualifier("priorityProcessingManagerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(priorityRawDataQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		processingManager.createShutDownHook(processingManagerContainer);
	}
}
