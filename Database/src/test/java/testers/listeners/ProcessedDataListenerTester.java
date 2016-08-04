package testers.listeners;

import repositories.*;
import data.*;
import testers.AbstractTester;

import java.util.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
* Unit test methods for ProcessedDataListener.
*
* @author Armand Maree
* @since 2016-07-24
*/
public class ProcessedDataListenerTester extends AbstractTester {
	private boolean setUpDone = false;

	private final static String processedDataQueueName = TestContext.processedDataQueueName;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	RabbitTemplate rabbitTemplate;

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
	public void testReceiveProcessedData() throws InterruptedException {
		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		acuben = userRepository.save(acuben);

		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "photo"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("Gmail", acuben.getGmailId(), null, "zsd5465sd4f65s4df65s4df65", ts, System.currentTimeMillis()));

		List<Topic> topicsBefore = topicRepository.findByUserId(acuben.getUserId());

		Assert.assertEquals("Failure - topicsBefore is not empty.", 0, topicsBefore.size());

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(5000);

		List<Topic> topicsAfter = topicRepository.findByUserId(acuben.getUserId());

		for (Topic topic : topicsAfter)
			System.out.println(topic.getTopic());

		Assert.assertEquals("Failure - topicsAfter is not empty.", 10, topicsAfter.size());
	}
}
