package testers.listeners;

import repositories.*;
import data.*;
import testers.AbstractTester;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.test.context.support.AnnotationConfigContextLoader;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

/**
* Unit test methods for BusinessListener.
*
* @author Armand Maree
* @since 2016-07-24
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=main.Application.class)
public class BusinessListenerTester extends AbstractTester {
	private boolean setUpDone = false;

	@Autowired
	private LinkedBlockingQueue<UserIdentified> queue;

	private final String userRegisterQueueName = "user-register.database.rabbit";
	private final String userCheckQueueName = "user-check.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Before
	public void setUp() throws InterruptedException {
		if (!setUpDone) {
			setUpDone = true;
		}

		userRepository.deleteAll();
		while (queue.poll(1, TimeUnit.SECONDS) != null);
	}

	@After
	public void tearDown() {
		
	}

	@Test
	public void testReceiveCheckIfRegistered() throws InterruptedException {
		User user = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified = new UserIdentified("0000", false, user);
		rabbitTemplate.convertAndSend(userCheckQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(10, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified.getReturnId(), userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failed - user is registered but shouldn't be.", false, userIdentifiedResponse.getIsRegistered());
	
		userRepository.save(user);

		rabbitTemplate.convertAndSend(userCheckQueueName, userIdentified);
		userIdentified = new UserIdentified("0000", false, user);
		rabbitTemplate.convertAndSend(userCheckQueueName, userIdentified);
		userIdentifiedResponse = queue.poll(10, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified.getReturnId(), userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failed - user isn't registered but should be.", true, userIdentifiedResponse.getIsRegistered());
	}

	@Test
	public void testReceiveUserRegister() throws InterruptedException {
		userRepository.deleteAll();

		User user = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified = new UserIdentified("0000", false, user);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(10, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified.getReturnId(), userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failed - user is registered but shouldn't be.", false, userIdentifiedResponse.getIsRegistered());
		Thread.sleep(1000);

		UserIdentified userIdentified2 = new UserIdentified("0001", false, user);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified2);
		UserIdentified userIdentifiedResponse2 = queue.poll(10, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse2 is null.", userIdentifiedResponse2);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified2.getReturnId(), userIdentifiedResponse2.getReturnId());
		Assert.assertEquals("Failed - user isn't registered but should be.", true, userIdentifiedResponse2.getIsRegistered());
		Assert.assertTrue("Failed - Users does not have same ID.", userIdentifiedResponse.getUser(true).getUserId().equals(userIdentifiedResponse2.getUser(true).getUserId()));
	}
}
