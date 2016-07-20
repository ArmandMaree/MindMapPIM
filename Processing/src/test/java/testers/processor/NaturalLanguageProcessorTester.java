package testers.processor;

import processor.*;
import data.*;
import testers.AbstractTester;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Unit test methods for the NaturalLanguageProcessor.
*
* @author Armand Maree
* @since 2016-07-20
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = main.Application.class)
public class NaturalLanguageProcessorTester extends AbstractTester {
	private RawData rawData;
	private ProcessedData processedData;
	private boolean setUpDone = false;

	@Autowired
	private NaturalLanguageProcessor nlp;

	@Before
	public void setUp() {
		if (!setUpDone) {
			String pimSource = "Gmail";
			String userId = "acubencos@gmail.com";
			String[] involvedContacts = {"susan@gmail.com", "steve@gmail.com", "thabo@gmail.com", "precious@gmail.com"};
			String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
			String[] data = {"Horse photo", "Hey Acuben, here is the photo you wanted."};
			long time = System.currentTimeMillis();
			rawData = new RawData(pimSource, userId, involvedContacts, pimItemId, data, time);
			String[] topics = {"horse", "photo", "Acuben"};
			processedData = new ProcessedData(rawData, topics);
			setUpDone = true;
		}
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testNLP() {
		Assert.assertNotNull("Failure - NLP is null.", nlp);
	}

	@Test
	public void testGetTopics() {
		ArrayList<String> topics = new ArrayList<>();
		Assert.assertNotNull("Failure - NLP is null.", nlp);

		for (String part : rawData.getData())
			topics.addAll(nlp.getTopics(part));

		topics = nlp.purge(topics);
		String[] topicsArr = topics.toArray(new String[0]);

		Assert.assertNotNull("Failure - topicsArr is null.", topicsArr);
		Assert.assertEquals("Failure - topics has wrong length.", processedData.getTopics().length, topicsArr.length);

		for (int i = 0; i < processedData.getTopics().length; i++)
			Assert.assertEquals("Failure - topics[" + i + "] differs.", processedData.getTopics()[i], topicsArr[i]);
	}
}
