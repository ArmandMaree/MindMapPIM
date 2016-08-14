package testers.listeners;

import nlp.*;
import data.*;
import listeners.*;
import testers.AbstractTester;

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
* Unit test methods for the RawDataListener.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = testers.listeners.TestContext.class)
public class ProcessingManagerTester extends AbstractTester {
	private boolean setUpDone = false;
	private final static String rawDataQueue = "raw-data.processing.rabbit";

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
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testRawDataListener() {
		Assert.assertNotNull("Failure - processingManager is null.", processingManager);
	}

	@Test
	public void testProcess() throws InterruptedException {
		String pimSource = "Gmail";
		String userId = "acubencos@gmail.com";
		String[] involvedContacts = {"Susan", "Steve"};
		String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
		String[] data = {"Horse photo", "Hey Acuben, here is the photo you wanted."};
		long time = System.currentTimeMillis();
		RawData rawData = new RawData(pimSource, userId, involvedContacts, pimItemId, data, time);
		String[] topics = {"horse", "photo", "Acuben"};
		rabbitTemplate.convertAndSend(rawDataQueue, rawData);

		ProcessedData processedData = processedDataQueue.poll(5, TimeUnit.SECONDS);

		Assert.assertNotNull("Failure - processedData is null.", processedData);
	}
}
