package testers.listeners;

import data.*;
import com.unclutter.poller.*;

import java.util.*;
import java.util.concurrent.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

@Configuration
public class TestContext {
	private final static String processedDataQueueName = "processed-data.database.rabbit";

	@Autowired
	private LinkedBlockingQueue<ProcessedData> processedDataQueue;

	@Bean
	LinkedBlockingQueue<ProcessedData> processedDataQueueLL() {
		return new LinkedBlockingQueue<>();
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
	Binding processedDataBinding(@Qualifier("processedDataQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(processedDataQueueName);
	}

	@Bean
	public MessageListenerAdapter processedDataAdapter() {
		return new MessageListenerAdapter(this, "receiveProcessedData");
	}

	@Bean
	public SimpleMessageListenerContainer processedDataContainer(ConnectionFactory connectionFactory, @Qualifier("processedDataAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(processedDataQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveProcessedData(ProcessedData processedData) {
		System.out.println("Test Received: " + processedData);
		try {
			processedDataQueue.put(processedData);
		}
		catch(InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
