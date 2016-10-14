package main;

import com.unclutter.poller.PollingConfiguration;
import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageBrokerFactory;

import listeners.AuthCodeListener;
import listeners.ItemListener;

import poller.GmailPoller;
import poller.GmailPollingUser;

import repositories.GmailRepository;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import org.springframework.stereotype.Component;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   1.0.0
*/
@SpringBootApplication
@ComponentScan({"com.unclutter.poller"})
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private GmailRepository gmailRepository;

	@Autowired
	private MessageBrokerFactory messageBrokerFactory;

	private MessageBroker messageBroker;

	@Bean
	public AuthCodeListener authCodeReceiver(GmailRepository gmailRepository) {
		return new AuthCodeListener(gmailRepository);
	}

	@Bean
	public ItemListener itemRequestReceiver(GmailRepository gmailRepository) {
		return new ItemListener(gmailRepository);
	}

	@Bean
	public MessageBrokerFactory messageBrokerFactory(RabbitTemplate rabbitTemplate, GmailRepository gmailRepository, AuthCodeListener authCodeListener, ItemListener itemListener) {
		PollingConfiguration pollingConfig = new PollingConfiguration("gmail", authCodeListener, "receiveAuthCode", itemListener, "receiveItemRequest");
		MessageBrokerFactory messageBrokerFactory = new MessageBrokerFactory(pollingConfig);
		messageBrokerFactory.setRabbitTemplate(rabbitTemplate);

		try {
			authCodeListener.setMessageBroker(messageBrokerFactory.getMessageBroker());
			itemListener.setMessageBroker(messageBrokerFactory.getMessageBroker());
			messageBroker = messageBrokerFactory.getMessageBroker();
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

	/**
	* Runs the {@link org.springframework.boot.CommandLineRunner} program.
	* <p>
	*	The commandline parameters that are supported are:
	*	<ul>
	*		<li>cleandb - This will clean the repository used by this poller.</li>
	*	</ul>
	* </p>
	*/
	@Override
	public void run(String... args) throws Exception {
		for (String arg : args) {
			switch (arg) {
				case "testaccount":
					System.out.println("Setting up test account.");
					GmailPoller poller = new GmailPoller(gmailRepository, messageBroker, "4/W4n6Kmnm1JP-WmVLwbG8cusspwoh2FCl0-iRXp8zoTo", "acubencos@gmail.com");
					new Thread(poller).start();
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

		List<GmailPollingUser> pollingUsers = gmailRepository.findByCurrentlyPolling(true);

		for (GmailPollingUser pollingUser : pollingUsers) {
			pollingUser.setCurrentlyPolling(false);
			gmailRepository.save(pollingUser);
			GmailPoller poller = new GmailPoller(gmailRepository, messageBroker, null, pollingUser.getUserId());
			new Thread(poller).start();
		}
	}
}
