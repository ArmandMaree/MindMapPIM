package testers.listeners;

import testers.AbstractTester;
import listeners.*;
import data.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
* Unit test methods for the FrontendListener.
*
* @author Armand Maree
* @since 2016-07-21
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = testers.listeners.TestContext.class)
public class FrontendListenerTester extends AbstractTester {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
	private final ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
	private final PrintStream stdout = System.out;
	private boolean setUpDone = false;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	FrontendListener frontendListener;

	@Autowired
	@Qualifier("testAuthCodeQueueDev")
	private LinkedBlockingQueue<AuthCode> authCodeQueue;

	@Autowired
	@Qualifier("testUserQueueDev")
	private LinkedBlockingQueue<UserIdentified> userIdentifiedQueue;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}
	}

	@After
	public void tearDown() throws InterruptedException {
		while (authCodeQueue.poll(1, TimeUnit.SECONDS) != null);
		while (userIdentifiedQueue.poll(1, TimeUnit.SECONDS) != null);
	}

	@Test
	public void testRabbitTemplate() {
		Assert.assertNotNull("Failure - rabbitTemplate is null. Is RabbitMQ running?", rabbitTemplate);
	}

	@Test
	public void testFrontendListener() {
		Assert.assertNotNull("Failure - frontendListener is null.", frontendListener);
	}

	@Test
	public void testReceiveRegister() throws InterruptedException {
		String id = UUID.randomUUID().toString();
		String firstName = "Acuben";
		String lastName = "Cos";
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", UUID.randomUUID().toString())};
		UserRegistrationIdentified userRegistrationIdentified = new UserRegistrationIdentified(id, firstName, lastName, authCodes);
		rabbitTemplate.convertAndSend(TestContext.registerBusinessQueueName, userRegistrationIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);
		UserIdentified userIdentified = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - userIdentified is null.", userIdentified);
	}

	@Test
	public void testReceiveUserUpdateRequest() throws InterruptedException {
		//test without gmail changing
		String id = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", UUID.randomUUID().toString())};
		UserUpdateRequestIdentified userUpdateIdentified = new UserUpdateRequestIdentified(id, userId, authCodes);
		rabbitTemplate.convertAndSend(TestContext.userUpdateBusinessQueueName, userUpdateIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);

		UserIdentified userIdentifiedResponse = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failure - returnIds no not match.", id, userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failure - userIds no not match.", userId, userIdentifiedResponse.getUserId());
	}
}
