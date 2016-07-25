package testers.listeners;

import testers.AbstractTester;
import listeners.*;
import data.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
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
@ContextConfiguration(classes = main.Application.class)
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
	@Qualifier("topicRequestContainer")
	SimpleMessageListenerContainer topicRequestContainer;

	@Autowired
	@Qualifier("topicResponseContainer")
	SimpleMessageListenerContainer topicResponseContainer;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}
	}

	@After
	public void tearDown() {

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
	public void testTopicRequestContainer() {
		Assert.assertNotNull("Failure - topicRequestContainer is null.", topicRequestContainer);
	}

	@Test
	public void testTopicResponseContainer() {
		Assert.assertNotNull("Failure - topicResponseContainer is null.", topicResponseContainer);
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
		String[] path = {"phone"};
		String[] exclude = {""};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);

		rabbitTemplate.convertAndSend("topic-request.business.rabbit", topicRequest);
	}
}
