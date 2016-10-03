package main;

import listeners.*;
import repositories.*;
import data.*;
import poller.*;

import com.unclutter.poller.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.PrintWriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
// import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.stereotype.Component;

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

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
@SpringBootApplication
@ComponentScan({"com.unclutter.poller"})
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	@Autowired
	private GmailRepository gmailRepository;

	@Bean
	public BusinessListener authCodeReceiver(GmailRepository gmailRepository) {
		return new BusinessListener(gmailRepository);
	}

	@Bean
	public FrontendListener itemRequestReceiver(GmailRepository gmailRepository) {
		return new FrontendListener(gmailRepository);
	}

	@Bean
	public MessageBrokerFactory messageBrokerFactory(RabbitTemplate rabbitTemplate, GmailRepository gmailRepository, BusinessListener business, FrontendListener frontend) {
		PollingConfiguration pollingConfig = new PollingConfiguration("gmail", business, "receiveAuthCode", frontend, "receiveItemRequest");
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
					// System.out.println("Setting up test account.");
					// GmailPoller poller = new GmailPoller(gmailRepository, rabbitTemplate, "4/W4n6Kmnm1JP-WmVLwbG8cusspwoh2FCl0-iRXp8zoTo", "acubencos@gmail.com", "http://codehaven.co.za/");
					// new Thread(poller).start();
					break;
				case "cleandb":
					System.out.println("Cleaning all databases...");
					gmailRepository.deleteAll();
					break;
				default:
					System.out.println("Invalid argument.");
					break;
			}
		}

		List<GmailPollingUser> pollingUsers = gmailRepository.findAll();

		for (GmailPollingUser pollingUser : pollingUsers) {
			if (pollingUser.getRefreshToken() != null && !pollingUser.getRefreshToken().equals("")) {
				GmailPoller poller = new GmailPoller(gmailRepository, null, null, pollingUser.getUserId());
				poller.setFirstId(pollingUser.getEarliestEmail());
				poller.setLastDate(pollingUser.getLastEmail());
				new Thread(poller).start();
			}
		}
	}
}
