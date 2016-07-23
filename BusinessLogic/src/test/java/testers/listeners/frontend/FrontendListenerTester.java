package testers.listeners.frontend;

import testers.AbstractTester;
import listeners.frontend.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

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
	private final ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
	private final PrintStream stdout = System.out;
	private boolean setUpDone = false;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	FrontendListener frontendListener;

	@Autowired
	SimpleMessageListenerContainer frontendListenerContainer;

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
	public void testFrontendListenerContainer() {
		Assert.assertNotNull("Failure - frontendListenerContainer is null.", frontendListenerContainer);
	}

	@Test
	public void testReceiveTopicRequest() throws InterruptedException {
		String userId = "6dsf5asf4as6df4s65df";
		String[] path = {"path1", "path2"};
		String[] exclude = {"exclude1", "exclude2"};
		int maxNumberOfTopics = 4;
		TopicRequest topicRequest = new TopicRequest(userId, path, exclude, maxNumberOfTopics);

		rabbitTemplate.convertAndSend("topicrequest.rabbit", topicRequest);
	}
}