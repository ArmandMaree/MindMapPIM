package main;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import poller.*;
import listeners.*;
import repositories.*;

import com.unclutter.poller.*;

@SpringBootApplication
@ComponentScan({"com.unclutter.poller"})
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	@Autowired
	MessageBrokerFactory messageBrokerFactory;

	@Autowired
	TwitterRepository twitterRepository;

	@Bean
	public BusinessListener authCodeReceiver(TwitterRepository twitterRepository) {
		return new BusinessListener(twitterRepository);
	}

	@Bean
	public FrontendListener itemRequestReceiver() {
		return new FrontendListener();
	}

	@Bean
	public MessageBrokerFactory messageBrokerFactory(RabbitTemplate rabbitTemplate, BusinessListener business, FrontendListener frontend) {
		PollingConfiguration pollingConfig = new PollingConfiguration("twitter", business, "receiveAuthCode", frontend, "receiveItemRequest");
		MessageBrokerFactory messageBrokerFactory = new MessageBrokerFactory(pollingConfig);
		messageBrokerFactory.setRabbitTemplate(rabbitTemplate);

		try {
			business.setMessageBroker(messageBrokerFactory.getMessageBroker());
			frontend.setMessageBroker(messageBrokerFactory.getMessageBroker());
		}
		catch (MessageBrokerFactory.BeansNotSetUpException bnsue) {
			bnsue.printStackTrace();
			System.exit(1);
		}
		return messageBrokerFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (String arg : args) {
			switch (arg) {
				case "testaccount":
					try {
						new Thread(new TwitterPoller(twitterRepository, messageBrokerFactory.getMessageBroker(), "cnnbrk")).start();
					}
					catch (AlreadyPollingForUserException apfue) {
						apfue.printStackTrace();
					}
					break;
				case "cleandb":
					twitterRepository.deleteAll();
					break;
			}
		}

		List<TwitterPollingUser> pollingUsers = twitterRepository.findByCurrentlyPolling(true);

		for (TwitterPollingUser pollingUser : pollingUsers) {
			try {
				new Thread(new TwitterPoller(twitterRepository, messageBrokerFactory.getMessageBroker(), pollingUser.getUserId())).start();
			}
			catch (AlreadyPollingForUserException apfue) {
				apfue.printStackTrace();
			}
		}
	}
}
