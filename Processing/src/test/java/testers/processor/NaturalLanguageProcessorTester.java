package testers.processor;

import nlp.*;
import data.*;
import testers.AbstractTester;

import java.util.List;
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
	public void testGetTopics() throws InterruptedException {
		String pimSource = "Gmail";
		String userId = "acubencos@gmail.com";
		List<String> involvedContacts = new ArrayList<>();
		involvedContacts.add("Susan Someone");
		involvedContacts.add("Steve Aoki");
		involvedContacts.add("Armand Maree");
		involvedContacts.add("Arno Grobler");
		String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
		String[] data = {"horse photo", "Hey Acuben, here is the photo you wanted of the horse."};
		long time = System.currentTimeMillis();
		RawData rawData = new RawData(pimSource, userId, involvedContacts, pimItemId, data, time);

		Assert.assertNotNull("Failure - NLP is null.", nlp);

		Processor processor = new Processor(null, null, null, nlp);
		ArrayList<String> topics = new ArrayList<>();
		
		for (String part : rawData.getData()) {
			ArrayList<String> topicsIdentified = nlp.getTopics(part);

			if (topicsIdentified == null)
				continue;

			for (String topic : topicsIdentified)
				topics.add(topic);
		}

		topics = nlp.purge(topics);
		ProcessedData processedData = new ProcessedData(rawData, topics.toArray(new String[0]));

		Assert.assertNotNull("Failure - processedData is null.", processedData);
		// Assert.assertEquals("Failure - topics has wrong length.", processedData.getTopics().length, topicsArr.length);
		System.out.println(processedData);
	}
}
