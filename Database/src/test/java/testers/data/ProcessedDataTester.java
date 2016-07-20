package testers.data;

import data.*;
import testers.AbstractTester;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
* Unit test methods for ProcessedData.
*
* @author Armand Maree
* @since 2016-07-20
*/
public class ProcessedDataTester extends AbstractTester {
	private RawData rawData;
	private ProcessedData processedData;
	private boolean setUpDone = false;

	private String[] topics = {"horse", "photo"};

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
			setUpDone = true;
		}
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testConstructor() {
		ProcessedData processedData = new ProcessedData(rawData, topics);

		Assert.assertEquals("Failure - pimSource differs.", rawData.getPimSource(), processedData.getPimSource());
		Assert.assertEquals("Failure - userId differs.", rawData.getUserId(), processedData.getUserId());
		Assert.assertEquals("Failure - involvedContacts length differs.", rawData.getInvolvedContacts().length, processedData.getInvolvedContacts().length);

		for (int i = 0; i < rawData.getInvolvedContacts().length; i++)
			Assert.assertEquals("Failure - involvedContacts[" + i + "] differs.", rawData.getInvolvedContacts()[i], processedData.getInvolvedContacts()[i]);

		Assert.assertEquals("Failure - pimItemId differs.", rawData.getPimItemId(), processedData.getPimItemId());
		Assert.assertEquals("Failure - topics length differs.", topics.length, processedData.getTopics().length);

		for (int i = 0; i < topics.length; i++)
			Assert.assertEquals("Failure - topics[" + i + "] differs.", topics[i], processedData.getTopics()[i]);

		Assert.assertEquals("Failure - time differs.", rawData.getTime(), processedData.getTime());
	}
}
