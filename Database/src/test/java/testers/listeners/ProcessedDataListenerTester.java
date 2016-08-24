package testers.listeners;

import repositories.*;
import data.*;
import testers.AbstractTester;

import java.util.*;

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
* Unit test methods for ProcessedDataListener.
*
* @author Armand Maree
* @since 2016-07-24
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=testers.listeners.TestContext.class)
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
	public void testReceiveProcessedData() throws InterruptedException {
		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		userRepository.save(acuben);
		acuben = userRepository.findByGmailId(acuben.getGmailId());

		Assert.assertNotNull("Failure - acuben is null.", acuben);

		List<ProcessedData> processedData = new ArrayList<>();

		String[][] processedDataTopics = {
			{"horse", "photo"},
			{"horse", "pizza"},
			{"horse", "pizza"},
			{"horse", "saddle"},
			{"horse", "pizza"},
			{"horse", "computer"},
			{"pizza", "book"},
			{"glass", "phone"},
			{"mouse", "pizza"},
			{"computer", "handle"}
		};

		for (String[] ts : processedDataTopics)
			processedData.add(new ProcessedData("Gmail", acuben.getGmailId(), null, UUID.randomUUID().toString(), ts, System.currentTimeMillis()));

		List<Topic> topicsBefore = topicRepository.findByUserId(acuben.getUserId());

		Assert.assertEquals("Failure - topicsBefore is not empty.", 0, topicsBefore.size());

		for (ProcessedData pd : processedData)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(10000);

		List<Topic> topicsAfter = topicRepository.findByUserId(acuben.getUserId());

		Assert.assertEquals("Failure - topicsAfter is not 10.", 10, topicsAfter.size());

		for (Topic topic : topicsAfter) {
			switch (topic.getTopic()) {
				case "horse":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to horse.", 4, topic.getRelatedTopics().size());
					break;
				case "photo":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to photo.", 1, topic.getRelatedTopics().size());
					break;
				case "saddle":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to saddle.", 1, topic.getRelatedTopics().size());
					break;
				case "pizza":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to pizza.", 3, topic.getRelatedTopics().size());
					break;
				case "computer":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to computer.", 2, topic.getRelatedTopics().size());
					break;
				case "book":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to book.", 1, topic.getRelatedTopics().size());
					break;
				case "glass":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to glass.", 1, topic.getRelatedTopics().size());
					break;
				case "phone":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to phone.", 1, topic.getRelatedTopics().size());
					break;
				case "mouse":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to mouse.", 1, topic.getRelatedTopics().size());
					break;
				case "handle":
					Assert.assertEquals("Failure - incorrect number of relatedTopics to handle.", 1, topic.getRelatedTopics().size());
					break;
			}
		}
	}

	@Test
	public void testInvolvedContacts() throws InterruptedException {
		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		userRepository.save(acuben);
		acuben = userRepository.findByGmailId(acuben.getGmailId());
		Assert.assertNotNull("Failure - acuben is null.", acuben);
		List<ProcessedData> processedDataList = new ArrayList<>();

		processedDataList.add(new ProcessedData("Gmail", acuben.getGmailId(), new String[]{"Armand Maree", "Danielle Stuart"}, UUID.randomUUID().toString(), new String[]{"horse", "beast"}, System.currentTimeMillis()));
		processedDataList.add(new ProcessedData("Gmail", acuben.getGmailId(), new String[]{"Amy Lochner", "Arno Grobler"}, UUID.randomUUID().toString(), new String[]{"computer", "ladder"}, System.currentTimeMillis()));
		processedDataList.add(new ProcessedData("Gmail", acuben.getGmailId(), new String[]{"Koos van der Merwe", "Steve Aoki"}, UUID.randomUUID().toString(), new String[]{"horse", "glasses"}, System.currentTimeMillis()));

		for (ProcessedData pd : processedDataList)
			rabbitTemplate.convertAndSend(processedDataQueueName, pd);

		Thread.sleep(5000);

		List<Topic> topicsAfter = topicRepository.findByUserIdAndPerson(acuben.getUserId(), false);
		Assert.assertEquals("Failure - topicsAfter is not 5.", 5, topicsAfter.size());

		List<Topic> contactsAfter = topicRepository.findByUserIdAndPerson(acuben.getUserId(), true);
		Assert.assertEquals("Failure - contactsAfter is not 6.", 6, contactsAfter.size());
	}
}
