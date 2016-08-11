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
@ContextConfiguration(classes = testers.listeners.TestContextTopicListener.class)
public class TopicListenerTester extends AbstractTester {
	private final static String processedDataQueueName = "processed-data.database.rabbit";
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

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testReceiveTopicRequest() throws InterruptedException {
		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "phone", "pizza"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("Gmail", "acubencos@gmail.com", null, "zsd5465sd4f65s4df65s4df65", ts, System.currentTimeMillis()));

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(5000);
		String userId = null;
		String[] path = {""};
		String[] exclude = {""};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);
		rabbitTemplate.convertAndSend("topic-request.database.rabbit", topicRequest);
		Thread.sleep(5000);

		TopicResponse topicResponse = topicResponseLinkedQueue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failure - topicResponse is null.", topicResponse);
	}
}
