package testers.listeners;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

import testers.AbstractTester;
import data.*;
import repositories.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
* Unit test methods for the TopicListener.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = testers.listeners.TestContext.class)
public class TopicListenerTester extends AbstractTester {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
	private final static String topicUpdateQueueName = "topic-update-request.database.rabbit";
	private final String userRegisterQueueName = TestContext.userRegisterQueueName;
	private boolean setUpDone = false;

	@Autowired
	private LinkedBlockingQueue<TopicResponse> topicResponseLinkedQueue;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private LinkedBlockingQueue<UserIdentified> queue;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}

		userRepository.deleteAll();
		processedDataRepository.deleteAll();
		topicRepository.deleteAll();
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testReceiveTopicRequest() throws InterruptedException {
		User user = new User("Acuben", "Cos");
		user.addPimId("gmail", "acubencos@gmail.com");
		user.addPimId("facebook", "acubenfacebook");
		UserIdentified userIdentified = new UserIdentified(UUID.randomUUID().toString(), false, user);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "photo"},
			{"horse", "pizza", "pizza", "pizza"},
			{"horse", "pizza", "pizza"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("gmail", user.getPimId("gmail"), null, UUID.randomUUID().toString(), ts, System.currentTimeMillis()));

		processedData.add(new ProcessedData("facebook", user.getPimId("facebook"), null, UUID.randomUUID().toString(), processedDataTopics[1], System.currentTimeMillis()));

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(10000);
		String userId = userIdentifiedResponse.getUserId();
		String[] path = {""};
		String[] exclude = {""};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);

		TopicResponse topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
		Assert.assertEquals("Failure - topicResponse does not have correct amount of topics.", 4, topicResponse.getTopics().length);

		path = new String[1];
		path[0] = "horse";
		topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);

		topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
		Assert.assertEquals("Failure - topicResponse does not have correct amount of topics.", 4, topicResponse.getTopics().length);
	}

	@Test
	public void testInvolvedContacts() throws InterruptedException {
		User acuben = new User("Acuben", "Cos");
		acuben.addPimId("gmail", "acubencos@gmail.com");
		UserIdentified userIdentified = new UserIdentified(UUID.randomUUID().toString(), false, acuben);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		List<ProcessedData> processedDataList = new ArrayList<>();

		processedDataList.add(new ProcessedData("gmail", acuben.getPimId("gmail"), new String[]{"Armand Maree", "Danielle Stuart"}, UUID.randomUUID().toString(), new String[]{"horse", "beast"}, System.currentTimeMillis()));
		processedDataList.add(new ProcessedData("gmail", acuben.getPimId("gmail"), new String[]{"Amy Lochner", "Arno Grobler"}, UUID.randomUUID().toString(), new String[]{"computer", "ladder"}, System.currentTimeMillis()));
		processedDataList.add(new ProcessedData("gmail", acuben.getPimId("gmail"), new String[]{"Koos van der Merwe", "Steve Aoki"}, UUID.randomUUID().toString(), new String[]{"horse", "glasses"}, System.currentTimeMillis()));

		for (ProcessedData pd : processedDataList)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(10000);
		String userId = userIdentifiedResponse.getUserId();
		String[] path = {""};
		String[] exclude = {""};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);

		TopicResponse topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
		Assert.assertEquals("Failure - topicResponse does not have correct amount of involvedContacts.", 4, topicResponse.getInvolvedContacts().length);

		path[0] = "horse";
		topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);

		topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
		Assert.assertEquals("Failure - topicResponse does not have correct amount of involvedContacts.", 2, topicResponse.getInvolvedContacts().length);
	}

	@Test
	public void receiveTopicUpdateRequest() throws InterruptedException {
		User user = new User("Acuben", "Cos");
		user.addPimId("gmail", "acubencos@gmail.com");
		user.addPimId("facebook", "acubenfacebook");
		UserIdentified userIdentified = new UserIdentified(UUID.randomUUID().toString(), false, user);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "photo"},
			{"horse", "pizza", "pizza", "pizza"},
			{"horse", "pizza", "pizza"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("gmail", user.getPimId("gmail"), null, UUID.randomUUID().toString(), ts, System.currentTimeMillis()));

		processedData.add(new ProcessedData("facebook", user.getPimId("facebook"), null, UUID.randomUUID().toString(), processedDataTopics[1], System.currentTimeMillis()));

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Topic topic = new Topic();
		topic.setTopic("horse");
		topic.setUserId(userIdentifiedResponse.getUserId());
		topic.setHidden(true);
		Thread.sleep(5000);
		rabbitTemplate.convertAndSend(topicUpdateQueueName, topic);
		Thread.sleep(5000);

		topic = topicRepository.findByTopicAndUserId(topic.getTopic(), topic.getUserId());

		Assert.assertNotNull("Failure - topic is null.", topic);
		Assert.assertTrue("Failure - topic is not hidden after update.", topic.getHidden());
	}
}
