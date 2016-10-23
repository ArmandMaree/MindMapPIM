package testers.processor;

import nlp.*;
import data.*;
import listeners.*;
import testers.AbstractTester;
import com.unclutter.poller.RawData;

import java.util.*;
import java.util.concurrent.*;

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
* Unit test methods for the Processor.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = testers.processor.TestContext.class)
public class ProcessorTester extends AbstractTester {
	private final static String rawDataQueue = "raw-data.processing.rabbit";
	private final static String priorityRawDataQueue = "priority-raw-data.processing.rabbit";
	private boolean setUpDone = false;

	@Autowired
	private ProcessingManager processingManager;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private LinkedBlockingQueue<ProcessedData> processedDataQueue;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}
	}

	@After
	public void tearDown() throws InterruptedException {
		// clean up after each test method
		while (processedDataQueue.poll(5, TimeUnit.SECONDS) != null);
	}

	@Test
	public void testRawDataProcess() throws InterruptedException {
		String pimSource = "Gmail";
		String userId = "acubencos@gmail.com";
		List<String> involvedContacts = new ArrayList<>();
		involvedContacts.add("Susan Someone");
		involvedContacts.add("Steve Aoki");
		String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
		String[] data = {"Horse photo", "Hey Acuben, here is the photo you wanted."};
		long time = System.currentTimeMillis();
		RawData rawData = new RawData(pimSource, userId, involvedContacts, pimItemId, data, time);
		List<String> topics = new ArrayList<>();
		topics.add("horse");
		topics.add("photo");
		topics.add("Acuben");
		rabbitTemplate.convertAndSend(rawDataQueue, rawData);
		ProcessedData processedData = processedDataQueue.poll(10, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - processedData is null.", processedData);

		Assert.assertEquals("Failure - correct amount of topics not retreived.", topics.size(), processedData.getTopics().length);

		for (String t : processedData.getTopics())
			Assert.assertTrue("Failure - topic \"" + t + "\" should not be in the list.", topics.contains(t));

		rabbitTemplate.convertAndSend(priorityRawDataQueue, rawData);
		processedData = processedDataQueue.poll(10, TimeUnit.SECONDS);
		Assert.assertNotNull("Failure - processedData is null.", processedData);
	}
}
