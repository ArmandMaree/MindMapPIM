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
			userRepository.deleteAll();
			processedDataRepository.deleteAll();
			topicRepository.deleteAll();
			setUpDone = true;
		}
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testReceiveTopicRequest() throws InterruptedException {
		User user = new User("Acuben", "Cos", "acubencos@gmail.com");
		UserIdentified userIdentified = new UserIdentified(UUID.randomUUID().toString(), false, user);
		rabbitTemplate.convertAndSend(userRegisterQueueName, userIdentified);
		UserIdentified userIdentifiedResponse = queue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failed - userIdentifiedResponse is null.", userIdentifiedResponse);
		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "pizza"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"},
			{"computer", "sock"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("Gmail", "acubencos@gmail.com", null, UUID.randomUUID().toString(), ts, System.currentTimeMillis()));

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(5000);
		String userId = userIdentifiedResponse.getUserId();
		String[] path = {""};
		String[] exclude = {""};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);
		Thread.sleep(5000);

		TopicResponse topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);
		System.out.println("Received: " + topicResponse);

		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
	}
}
