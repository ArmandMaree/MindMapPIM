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
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.user.*;
import repositories.pimprocesseddata.*;
import listeners.*;
import data.*;

@SpringBootApplication
@EnableRabbit
public class Application implements CommandLineRunner {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Bean
	Queue topicRequestQueue() {
		return new Queue("topicrequest.rabbit", false);
	}

	@Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

	@Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

	@Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

	@Bean
    public FanoutExchange topicRequestExchange() {
        return new FanoutExchange("topicRequest");
    }

	@Bean
    public Binding topicRequestBinding(Queue topicRequestQueue, FanoutExchange topicRequestExchange) {
        return BindingBuilder.bind(topicRequestQueue).to(topicRequestExchange);
    }

	@Bean
    SimpleMessageListenerContainer frontendListenerContainer(ConnectionFactory connectionFactory, @Qualifier("persistenceListenerAdapter") MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(topicRequestQueue);
        container.setMessageListener(listenerAdapter);
        return container;
    }

	@Bean
    MessageListenerAdapter FrontendListenerAdapter(FrontendListener frontendListener) {
        return new MessageListenerAdapter(frontendListener, "receiveTopicRequest");
    }

	@Bean
    FrontendListener frontendListener() {
        return new FrontendListener();
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
