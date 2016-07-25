package testers.listeners;

import nlp.*;
import data.*;
import listeners.*;
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
* Unit test methods for the RawDataListener.
*
* @author Armand Maree
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = main.Application.class)
public class RawDataListenerTester extends AbstractTester {
	private RawData rawData;
	private ProcessedData processedData;
	private boolean setUpDone = false;

	@Autowired
	private RawDataListener rawDataListener;

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
	public void testRawDataListener() {
		Assert.assertNotNull("Failure - rawDataListener is null.", rawDataListener);
	}

	@Test
	public void testProcess() {
		ProcessedData pd = rawDataListener.process(rawData);

		Assert.assertNotNull("Failure - processedData is null.", pd);
		Assert.assertEquals("Failure - pimSource is not equal.", processedData.getPimSource(), pd.getPimSource());
		Assert.assertEquals("Failure - userId is not equal.", processedData.getUserId(), pd.getUserId());
		Assert.assertEquals("Failure - involvedContacts length differs.", processedData.getInvolvedContacts().length, pd.getInvolvedContacts().length);

		for (int i = 0; i < processedData.getInvolvedContacts().length; i++)
			Assert.assertEquals("Failure - involvedContacts[" + i + "] differs.", processedData.getInvolvedContacts()[i], pd.getInvolvedContacts()[i]);

		Assert.assertEquals("Failure - pimItemId is not equal.", processedData.getPimItemId(), pd.getPimItemId());
		Assert.assertEquals("Failure - time is not equal.", processedData.getTime(), pd.getTime());
		Assert.assertEquals("Failure - topics length differs.", processedData.getTopics().length, pd.getTopics().length);

		for (int i = 0; i < processedData.getTopics().length; i++)
			Assert.assertEquals("Failure - topics[" + i + "] differs.", processedData.getTopics()[i], pd.getTopics()[i]);
	}
}
