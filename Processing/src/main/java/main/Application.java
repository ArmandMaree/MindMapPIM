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

import processor.*;
import data.*;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   2016-07-14
*/
@SpringBootApplication
public class Application implements CommandLineRunner {
	final static String queueName = "raw-data.processing.rabbit";

	@Autowired
	NaturalLanguageProcessor nlpG;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	DataProcessor dataProcessor(NaturalLanguageProcessor nlp, RabbitTemplate rabbitTemplate) {
		return new DataProcessor(nlp, rabbitTemplate);
	}

	@Bean
	MessageListenerAdapter listenerAdapter(DataProcessor dataProcessor) {
		return new MessageListenerAdapter(dataProcessor, "receiveRawData");
	}

	@Bean
	NaturalLanguageProcessor naturalLanguageProcessor() {
		return new StanfordNLP();
		// return null;
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// System.out.println("Waiting five seconds...");
		// Thread.sleep(5000);
		// System.out.println("Sending message...");
		// String[] data = {"cheesecake", "horse", "beach"};
		// RawData rawData = new RawData(null, null, null, null, data);
		// rabbitTemplate.convertAndSend(queueName, rawData);
		// dataProcessor(nlpG).getLatch().await(10000, TimeUnit.MILLISECONDS);
	}
}
