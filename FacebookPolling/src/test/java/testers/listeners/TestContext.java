package testers.listeners;

import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.*;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import data.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class TestContext {
	public static final String itemResponseQueueName = "item-response.frontend.rabbit";

	@Autowired
	@Qualifier("itemResponseQueueLLBean")
	private LinkedBlockingQueue<ItemResponseIdentified> itemResponseQueueLL;

	@Bean
	LinkedBlockingQueue<ItemResponseIdentified> itemResponseQueueLLBean() {
		return new LinkedBlockingQueue<>();
	}

	@Bean
	Queue itemResponseQueue() {
		return new Queue(itemResponseQueueName, false);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}

	@Bean
	Binding itemResponseBinding(@Qualifier("itemResponseQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(itemResponseQueueName);
	}

	@Bean
	public MessageListenerAdapter itemResponseAdapter() {
		return new MessageListenerAdapter(this, "receiveItemResponse");
	}

	@Bean
	public SimpleMessageListenerContainer itemResponseContainer(ConnectionFactory connectionFactory, @Qualifier("itemResponseAdapter") MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(itemResponseQueueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	public void receiveItemResponse(ItemResponseIdentified itemResponseIdentified) {
		System.out.println("TestContext received: " + itemResponseIdentified);
		
		try {
			itemResponseQueueLL.put(itemResponseIdentified);
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}
