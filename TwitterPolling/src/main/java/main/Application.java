package main;

import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageBrokerFactory;
import com.unclutter.poller.PollingConfiguration;

import java.util.List;

import listeners.AuthCodeListener;
import listeners.ItemListener;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import poller.AlreadyPollingForUserException;
import poller.TwitterPoller;
import poller.TwitterPollingUser;

import repositories.TwitterRepository;

/**
* Main Spring Boot application that runs and creates all the bean.
*
* @author  Armand Maree
* @since   1.0.0
*/
@SpringBootApplication
@ComponentScan({"com.unclutter.poller"})
@EnableMongoRepositories({"repositories"})
public class Application implements CommandLineRunner {
	@Autowired
	MessageBrokerFactory messageBrokerFactory;

	@Autowired
	TwitterRepository twitterRepository;

	@Bean
	public AuthCodeListener authCodeReceiver(TwitterRepository twitterRepository) {
		return new AuthCodeListener(twitterRepository);
	}

	@Bean
	public ItemListener itemRequestReceiver() {
		return new ItemListener();
	}

	@Bean
	public MessageBrokerFactory messageBrokerFactory(RabbitTemplate rabbitTemplate, AuthCodeListener authCodeListener, ItemListener itemListener) {
		PollingConfiguration pollingConfig = new PollingConfiguration("twitter", authCodeListener, "receiveAuthCode", itemListener, "receiveItemRequest");
		MessageBrokerFactory messageBrokerFactory = new MessageBrokerFactory(pollingConfig);
		messageBrokerFactory.setRabbitTemplate(rabbitTemplate);

		try {
			authCodeListener.setMessageBroker(messageBrokerFactory.getMessageBroker());
			itemListener.setMessageBroker(messageBrokerFactory.getMessageBroker());
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
					try {
						new Thread(new TwitterPoller(twitterRepository, messageBrokerFactory.getMessageBroker(), "cnnbrk")).start();
					}
					catch (AlreadyPollingForUserException apfue) {
						apfue.printStackTrace();
					}
					break;
				case "cleandb":
					System.out.println("Cleaning Twitter's database.");
					twitterRepository.deleteAll();
					break;
			}
		}

		List<TwitterPollingUser> pollingUsers = twitterRepository.findByCurrentlyPolling(true);

		for (TwitterPollingUser pollingUser : pollingUsers) {
			try {
				pollingUser.setCurrentlyPolling(false);
				twitterRepository.save(pollingUser);
				new Thread(new TwitterPoller(twitterRepository, messageBrokerFactory.getMessageBroker(), pollingUser.getUserId())).start();
			}
			catch (AlreadyPollingForUserException apfue) {
				apfue.printStackTrace();
			}
		}
	}
}
