package testers.main;

import data.*;
import repositories.*;
import testers.AbstractTester;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Unit test methods for the Application.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = main.Application.class)
public class ApplicationTester extends AbstractTester {
	private boolean setUpDone = false;

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
	public void testBeans() {
		Assert.assertNotNull("Failure - rabbitTemplate is null. Is RabbitMQ running?", rabbitTemplate);
		Assert.assertNotNull("Failure - userRepository is null. Is MongoDB running?", userRepository);
		Assert.assertNotNull("Failure - processedDataRepository is null. Is MongoDB running?", processedDataRepository);
		Assert.assertNotNull("Failure - topicRepository is null. Is MongoDB running?", topicRepository);
	}
}
