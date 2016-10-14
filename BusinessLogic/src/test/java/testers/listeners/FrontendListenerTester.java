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
	@Qualifier("testAuthCodeQueueDev")
	private LinkedBlockingQueue<AuthCode> authCodeQueue;

	@Autowired
	@Qualifier("testUserQueueDev")
	private LinkedBlockingQueue<UserIdentified> userIdentifiedQueue;

	@Autowired
	@Qualifier("testTopicRequestQueueDev")
	private LinkedBlockingQueue<TopicRequest> topicRequestQueue;

	@Autowired
	@Qualifier("testTopicResponseQueueDev")
	private LinkedBlockingQueue<TopicResponse> topicResponseQueue;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}
	}

	@After
	public void tearDown() throws InterruptedException {

	}

	@Test
	public void testReceiveTopicRequest() throws InterruptedException {
		TopicRequest topicRequest = new TopicRequest();
		rabbitTemplate.convertAndSend(TestContext.topicRequestBusinessQueueName, topicRequest);
		TopicRequest topicRequestReceive = topicRequestQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - topicRequest is not being sent.", topicRequestReceive);
	}

	@Test
	public void testReceiveTopicResponse() throws InterruptedException {
		TopicResponse topicResponse = new TopicResponse(null, null, null, null);
		rabbitTemplate.convertAndSend(TestContext.topicResponseBusinessQueueName, topicResponse);
		TopicResponse topicResponseReceive = topicResponseQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - topicResponse is not being sent.", topicResponseReceive);
	}

	@Test
	public void testReceiveRegisterOnePIM() throws InterruptedException {
		String id = UUID.randomUUID().toString();
		String firstName = "Acuben";
		String lastName = "Cos";
		String authCodeGmail = UUID.randomUUID().toString();
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", authCodeGmail)};
		UserRegistrationIdentified userRegistrationIdentified = new UserRegistrationIdentified(id, firstName, lastName, authCodes);
		rabbitTemplate.convertAndSend(TestContext.registerBusinessQueueName, userRegistrationIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);
		Assert.assertEquals("Failed - authCode does not match.", authCodeGmail, authCode.getAuthCode());
		UserIdentified userIdentified = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - userIdentified is null.", userIdentified);
	}

	@Test
	public void testReceiveRegisterTwoPIMs() throws InterruptedException {
		String id = UUID.randomUUID().toString();
		String firstName = "Acuben";
		String lastName = "Cos";
		String authCodeGmail = UUID.randomUUID().toString();
		String authCodeFacebook = UUID.randomUUID().toString();
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", authCodeGmail), new AuthCode("544654654654654", "facebook", authCodeFacebook)};
		UserRegistrationIdentified userRegistrationIdentified = new UserRegistrationIdentified(id, firstName, lastName, authCodes);
		rabbitTemplate.convertAndSend(TestContext.registerBusinessQueueName, userRegistrationIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);
		System.out.println("First AuthCode: " + authCode);

		if (authCode.getPimSource().equals("gmail"))
			Assert.assertEquals("Failed - authCode does not match.", authCodeGmail, authCode.getAuthCode());
		else
			Assert.assertEquals("Failed - authCode does not match.", authCodeFacebook, authCode.getAuthCode());

		authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);

		if (authCode.getPimSource().equals("gmail"))
			Assert.assertEquals("Failed - authCode does not match.", authCodeGmail, authCode.getAuthCode());
		else
			Assert.assertEquals("Failed - authCode does not match.", authCodeFacebook, authCode.getAuthCode());

		UserIdentified userIdentified = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - userIdentified is null.", userIdentified);
	}

	@Test
	public void testReceiveRegisterNoPIMs() throws InterruptedException {
		String id = UUID.randomUUID().toString();
		String firstName = "Acuben";
		String lastName = "Cos";
		AuthCode[] authCodes = {};
		UserRegistrationIdentified userRegistrationIdentified = new UserRegistrationIdentified(id, firstName, lastName, authCodes);
		rabbitTemplate.convertAndSend(TestContext.registerBusinessQueueName, userRegistrationIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNull("Failed - authCode is not null.", authCode);
		UserIdentified userIdentified = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - userIdentified is null.", userIdentified);
	}

	@Test
	public void testReceiveUserUpdateRequestNoChange() throws InterruptedException {
		//test without gmail changing
		String id = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		AuthCode[] authCodes = {};
		UserUpdateRequestIdentified userUpdateIdentified = new UserUpdateRequestIdentified(id, userId, authCodes);
		rabbitTemplate.convertAndSend(TestContext.userUpdateBusinessQueueName, userUpdateIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNull("Failed - authCode is not null.", authCode);

		UserIdentified userIdentifiedResponse = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failure - returnIds no not match.", id, userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failure - userIds no not match.", userId, userIdentifiedResponse.getUserId());
	}

	@Test
	public void testReceiveUserUpdateRequestRemovePIM() throws InterruptedException {
		//test without gmail changing
		String id = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", "")};
		UserUpdateRequestIdentified userUpdateIdentified = new UserUpdateRequestIdentified(id, userId, authCodes);
		rabbitTemplate.convertAndSend(TestContext.userUpdateBusinessQueueName, userUpdateIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);
		Assert.assertEquals("Failed - id differs.", authCodes[0].getId(), authCode.getId());
		Assert.assertEquals("Failed - pimSource differs.", authCodes[0].getPimSource(), authCode.getPimSource());
		Assert.assertEquals("Failed - authCode differs.", authCodes[0].getAuthCode(), authCode.getAuthCode());

		UserIdentified userIdentifiedResponse = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failure - returnIds no not match.", id, userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failure - userIds no not match.", userId, userIdentifiedResponse.getUserId());
		Assert.assertEquals("Failed - pimSource differs.", 1, userIdentifiedResponse.getPimIds().size());
		Assert.assertEquals("Failed - id differs.", "", userIdentifiedResponse.getPimIds().get(0).uId);
		Assert.assertEquals("Failed - pimSource differs.", authCodes[0].getPimSource(), userIdentifiedResponse.getPimIds().get(0).pim);
	}

	@Test
	public void testReceiveUserUpdateRequestAddPIM() throws InterruptedException {
		//test without gmail changing
		String id = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		AuthCode[] authCodes = {new AuthCode("acubencos@gmail.com", "gmail", UUID.randomUUID().toString())};
		UserUpdateRequestIdentified userUpdateIdentified = new UserUpdateRequestIdentified(id, userId, authCodes);
		rabbitTemplate.convertAndSend(TestContext.userUpdateBusinessQueueName, userUpdateIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failed - authCode is null.", authCode);
		Assert.assertEquals("Failed - id differs.", authCodes[0].getId(), authCode.getId());
		Assert.assertEquals("Failed - pimSource differs.", authCodes[0].getPimSource(), authCode.getPimSource());
		Assert.assertEquals("Failed - authCode differs.", authCodes[0].getAuthCode(), authCode.getAuthCode());

		UserIdentified userIdentifiedResponse = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failure - returnIds no not match.", id, userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failure - userIds no not match.", userId, userIdentifiedResponse.getUserId());
		Assert.assertEquals("Failed - pimSource differs.", 1, userIdentifiedResponse.getPimIds().size());
		Assert.assertEquals("Failed - id differs.", authCodes[0].getId(), userIdentifiedResponse.getPimIds().get(0).uId);
		Assert.assertEquals("Failed - pimSource differs.", authCodes[0].getPimSource(), userIdentifiedResponse.getPimIds().get(0).pim);
	}

	@Test
	public void testReceiveUserUpdateRequestDetailsChange() throws InterruptedException {
		//test without gmail changing
		String id = UUID.randomUUID().toString();
		String userId = UUID.randomUUID().toString();
		AuthCode[] authCodes = {};
		UserUpdateRequestIdentified userUpdateIdentified = new UserUpdateRequestIdentified(id, userId, authCodes);
		userUpdateIdentified.setBranchingFactor(20);
		userUpdateIdentified.setInitialDepth(32);
		String[] theme = {"t1", "t2", "t3"};
		userUpdateIdentified.setTheme(theme);
		rabbitTemplate.convertAndSend(TestContext.userUpdateBusinessQueueName, userUpdateIdentified);

		AuthCode authCode = authCodeQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNull("Failed - authCode is not null.", authCode);

		UserIdentified userIdentifiedResponse = userIdentifiedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - userIdentifiedResponse is null.", userIdentifiedResponse);
		Assert.assertEquals("Failure - returnIds no not match.", id, userIdentifiedResponse.getReturnId());
		Assert.assertEquals("Failure - userIds no not match.", userId, userIdentifiedResponse.getUserId());
		Assert.assertEquals("Failure - branchingFactor is not the same.", userUpdateIdentified.getBranchingFactor(), userIdentifiedResponse.getBranchingFactor());
		Assert.assertEquals("Failure - depthFactor is not the same.", userUpdateIdentified.getInitialDepth(), userIdentifiedResponse.getInitialDepth());
		Assert.assertEquals("Failure - theme[0] is not the same.", userUpdateIdentified.getTheme()[0], userIdentifiedResponse.getTheme()[0]);
		Assert.assertEquals("Failure - theme[1] is not the same.", userUpdateIdentified.getTheme()[1], userIdentifiedResponse.getTheme()[1]);
		Assert.assertEquals("Failure - theme[2] is not the same.", userUpdateIdentified.getTheme()[2], userIdentifiedResponse.getTheme()[2]);
	}
}
