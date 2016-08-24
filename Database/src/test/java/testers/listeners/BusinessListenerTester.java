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
@ContextConfiguration(classes=testers.listeners.TestContext.class)
public class BusinessListenerTester extends AbstractTester {
	private boolean setUpDone = false;
	private final String userRegisterQueueName = TestContext.userRegisterQueueName;
	private final String userCheckQueueName = TestContext.userCheckQueueName;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private LinkedBlockingQueue<UserIdentified> queue;

	@Autowired
	private LinkedBlockingQueue<UserUpdateResponseIdentified> userUpdateQueue;

	@Before
	public void setUp() throws InterruptedException {
		if (!setUpDone) {
			while (userUpdateQueue.poll(1, TimeUnit.SECONDS) != null);
			while (queue.poll(1, TimeUnit.SECONDS) != null);
			setUpDone = true;
		}

		userRepository.deleteAll();
	}

	@After
	public void tearDown() throws InterruptedException {

	}

	@Test
	public void testReceiveCheckIfRegistered() throws InterruptedException {
		User user1 = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified1 = new UserIdentified("0000", false, user1);
		rabbitTemplate.convertAndSend(userCheckQueueName, userIdentified1);
		UserIdentified userIdentifiedResponse1 = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse1);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified1.getReturnId(), userIdentifiedResponse1.getReturnId());
		Assert.assertEquals("Failed - user is registered but shouldn't be.", false, userIdentifiedResponse1.getIsRegistered());

		userRepository.save(user1);
		// Thread.sleep(1000);

		User user2 = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified2 = new UserIdentified("0001", false, user2);
		rabbitTemplate.convertAndSend(userCheckQueueName, userIdentified2);
		UserIdentified userIdentifiedResponse2 = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse2);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified2.getReturnId(), userIdentifiedResponse2.getReturnId());
		Assert.assertEquals("Failed - user isn't registered but should be.", true, userIdentifiedResponse2.getIsRegistered());
	}

	@Test
	public void testReceiveUserRegister() throws InterruptedException {
		User user1 = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified = new UserIdentified("0002", false, user1);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified.getReturnId(), userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failed - user is registered but shouldn't be.", false, userIdentifiedResponse.getIsRegistered());
		// Thread.sleep(2000);

		User user2 = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified2 = new UserIdentified("0003", false, user2);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified2);
		UserIdentified userIdentifiedResponse2 = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse2 is null.", userIdentifiedResponse2);
		Assert.assertEquals("Failed - returnId does not match.", userIdentified2.getReturnId(), userIdentifiedResponse2.getReturnId());
		Assert.assertEquals("Failed - user isn't registered but should be.", true, userIdentifiedResponse2.getIsRegistered());
		Assert.assertTrue("Failed - Users does not have same ID.", userIdentifiedResponse.getUserId().equals(userIdentifiedResponse2.getUserId()));
	}

	@Test
	public void testReceiveUserUpdate() throws InterruptedException {
		// update user that doesnt exist
		UserIdentified userIdentifiedRequest = new UserIdentified(UUID.randomUUID().toString(), UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, userIdentifiedRequest);

		UserUpdateResponseIdentified uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - returnIds does not match.", userIdentifiedRequest.getReturnId(), uuResponseIdentified.getReturnId());
		Assert.assertEquals("Failure - User was found but shouln't have.", UserUpdateResponse.USER_NOT_FOUND, uuResponseIdentified.getCode());

		// update a user that does not require any updates
		UserIdentified user = new UserIdentified(UUID.randomUUID().toString(), false, "Acuben", "Cos", "acubencos@gmail.com");
		rabbitTemplate.convertAndSend(userRegisterQueueName, user);
		user = queue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", user);

		UserIdentified updatesUser = new UserIdentified(UUID.randomUUID().toString(), user.getUserId());
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, updatesUser);

		uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - uuResponseIdentified did not return success, but should have.", UserUpdateResponse.SUCCESS, uuResponseIdentified.getCode());

		User userInRepo = userRepository.findByUserId(user.getUserId());
		Assert.assertEquals("Failure - userInRepo's gmailId changed and shouldn't have.", user.getGmailId(), userInRepo.getGmailId());
		Assert.assertEquals("Failure - userInRepo's theme[0] changed and shouldn't have.", user.getTheme()[0], userInRepo.getTheme()[0]);
		Assert.assertEquals("Failure - userInRepo's theme[1] changed and shouldn't have.", user.getTheme()[1], userInRepo.getTheme()[1]);
		Assert.assertEquals("Failure - userInRepo's branchingFactor changed and shouldn't have.", user.getBranchingFactor(), userInRepo.getBranchingFactor());
		Assert.assertEquals("Failure - userInRepo's initialDepth changed and shouldn't have.", user.getInitialDepth(), userInRepo.getInitialDepth());

		// change user's gmailId
		updatesUser = new UserIdentified(UUID.randomUUID().toString(), user.getUserId());
		updatesUser.setGmailId("test@example.com");
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, updatesUser);

		uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - uuResponseIdentified did not return success, but should have.", UserUpdateResponse.SUCCESS, uuResponseIdentified.getCode());

		userInRepo = userRepository.findByUserId(user.getUserId());
		Assert.assertEquals("Failure - userInRepo's gmailId did not change but should have.", updatesUser.getGmailId(), userInRepo.getGmailId());

		// change user's theme
		updatesUser = new UserIdentified(UUID.randomUUID().toString(), user.getUserId());
		String[] updateTheme = {"test1", "test2"};
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, updatesUser);

		uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - uuResponseIdentified did not return success, but should have.", UserUpdateResponse.SUCCESS, uuResponseIdentified.getCode());

		userInRepo = userRepository.findByUserId(user.getUserId());
		Assert.assertEquals("Failure - userInRepo's theme[0] did not change but should have.", user.getTheme()[0], userInRepo.getTheme()[0]);
		Assert.assertEquals("Failure - userInRepo's theme[1] did not change but should have.", updatesUser.getTheme()[1], userInRepo.getTheme()[1]);

		// change user's branchingFactor
		updatesUser = new UserIdentified(UUID.randomUUID().toString(), user.getUserId());
		updatesUser.setBranchingFactor(10);
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, updatesUser);

		uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - uuResponseIdentified did not return success, but should have.", UserUpdateResponse.SUCCESS, uuResponseIdentified.getCode());

		userInRepo = userRepository.findByUserId(user.getUserId());
		Assert.assertEquals("Failure - userInRepo's branchingFactor did not change but should have.", updatesUser.getBranchingFactor(), userInRepo.getBranchingFactor());

		// change user's branchingFactor
		updatesUser = new UserIdentified(UUID.randomUUID().toString(), user.getUserId());
		updatesUser.setInitialDepth(5);
		rabbitTemplate.convertAndSend(TestContext.userUpdateRequestQueueName, updatesUser);

		uuResponseIdentified = userUpdateQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userUpdateResponseIdentified is null.", uuResponseIdentified);
		Assert.assertEquals("Failure - uuResponseIdentified did not return success, but should have.", UserUpdateResponse.SUCCESS, uuResponseIdentified.getCode());

		userInRepo = userRepository.findByUserId(user.getUserId());
		Assert.assertEquals("Failure - userInRepo's initialDepth did not change but should have.", updatesUser.getInitialDepth(), userInRepo.getInitialDepth());
	}
}
