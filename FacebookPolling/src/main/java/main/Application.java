package main;

import com.unclutter.poller.PollingConfiguration;
import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageBrokerFactory;

import listeners.AuthCodeListener;
import listeners.ItemListener;

import java.util.List;

import poller.AlreadyPollingForUserException;
import poller.FacebookPoller;
import poller.FacebookPollingUser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import repositories.FacebookRepository;

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
	private MessageBrokerFactory messageBrokerFactory;

	@Autowired
	private FacebookRepository facebookRepository;

	@Bean
	public AuthCodeListener authCodeReceiver(FacebookRepository facebookRepository) {
		return new AuthCodeListener(facebookRepository);
	}

	@Bean
	public ItemListener itemRequestReceiver() {
		return new ItemListener();
	}

	@Bean
	public MessageBrokerFactory messageBrokerFactory(RabbitTemplate rabbitTemplate, AuthCodeListener authCodeListener, ItemListener itemListener) {
		PollingConfiguration pollingConfig = new PollingConfiguration("facebook", authCodeListener, "receiveAuthCode", itemListener, "receiveItemRequest");
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
				case "cleandb":
					System.out.println("Cleaning Facebook's database.");
					facebookRepository.deleteAll();
					break;
			}
		}

		List<FacebookPollingUser> pollingUsers = facebookRepository.findByCurrentlyPolling(true);

		for (FacebookPollingUser pollingUser : pollingUsers) {
			try {
				new Thread(new FacebookPoller(facebookRepository, messageBrokerFactory.getMessageBroker(), pollingUser.getAccessToken(), pollingUser.getExpireTime(), pollingUser.getUserId())).start();
			}
			catch (AlreadyPollingForUserException apfue) {
				apfue.printStackTrace();
			}
		}
	}
}
