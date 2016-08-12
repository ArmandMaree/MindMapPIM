package testers.listeners;

import listeners.FrontendListener;
import testers.AbstractTester;
import data.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
/**
* Unit test methods for the GmailPoller.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = main.Application.class)
public class FrontendListenerTester extends AbstractTester {
	private final String itemRequestQueueName = "item-request.gmail.rabbit";
	private boolean setUpDone = false;

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
	public void testBeans() throws InterruptedException { // manual test
		String[] itemIds = {UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
		ItemRequestIdentified itemRequestIdentified = new ItemRequestIdentified(UUID.randomUUID().toString(), itemIds, "acubencos@gmail.com");

		rabbitTemplate.convertAndSend(itemRequestQueueName, itemRequestIdentified);
		Thread.sleep(5000);
	}
}
