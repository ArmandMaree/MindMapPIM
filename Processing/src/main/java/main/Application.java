package main;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

import nlp.*;
import data.*;
import listeners.*;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   2016-07-14
*/
@SpringBootApplication
public class Application implements CommandLineRunner {
	final static String rawDataQueueName = "raw-data.processing.rabbit";

	@Autowired
	NaturalLanguageProcessor nlp;

	@Autowired
	RabbitTemplate rabbitTemplate;

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
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding rawDataBinding(@Qualifier("rawDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(rawDataQueueName);
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
	SimpleMessageListenerContainer processingManagerContainer(ConnectionFactory connectionFactory, @Qualifier("processingManagerAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(rawDataQueueName);
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
