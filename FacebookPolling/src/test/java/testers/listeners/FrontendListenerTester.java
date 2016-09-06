package testers.listeners;

import listeners.FrontendListener;
import testers.AbstractTester;
import data.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
/**
* Unit test methods for the FacebookPoller.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = testers.listeners.TestContext.class)
public class FrontendListenerTester extends AbstractTester {
	private final String itemRequestQueueName = "item-request.facebook.rabbit";
	private boolean setUpDone = false;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	@Qualifier("itemResponseQueueLLBean")
	LinkedBlockingQueue<ItemResponseIdentified> itemResponseQueue;

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
	public void testReceiveItemRequest() throws InterruptedException { // manual test
		String userId = "acubencos@gmail.com";
		String[] itemIds = {"facebook", UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString()};
		ItemRequestIdentified itemRequestIdentified = new ItemRequestIdentified(UUID.randomUUID().toString(), itemIds, userId);

		rabbitTemplate.convertAndSend(itemRequestQueueName, itemRequestIdentified);
		ItemResponseIdentified itemResponseIdentified = itemResponseQueue.poll(5, TimeUnit.SECONDS);
		System.out.println("Received: " + itemResponseIdentified);
		Assert.assertNotNull("Failure - itemResponseIdentified is null.", itemResponseIdentified);
		Assert.assertEquals("Failure - itemResponseIdentified did not return the correct amount of items", itemIds.length, itemResponseIdentified.getItems().length);

		for (int i = 0; i < itemResponseIdentified.getItems().length; i++) {
			String testString = "<iframe class=\"facebook-iframe\" src=\"https://www.facebook.com/plugins/post.php?href=https://www.facebook.com/" + userId + "/posts/" + itemIds[i] + "/&amp;width=500\"></iframe>";

			if (i == 0)
				testString = "facebook";

			Assert.assertEquals("Failure - itemResponseIdentified item[" + i + "] does not contain the correct item.", testString, itemResponseIdentified.getItems()[i]);
		}
	}
}
