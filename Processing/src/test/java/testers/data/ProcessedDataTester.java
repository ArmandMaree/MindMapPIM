package testers.data;

import data.*;
import com.unclutter.poller.RawData;
import testers.AbstractTester;

import java.util.List;
import java.util.ArrayList;

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
	private boolean setUpDone = false;

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
	public void testConstructor() {
		String pimSource = "Gmail";
		String userId = "acubencos@gmail.com";
		List<String> involvedContacts = new ArrayList<>();
		involvedContacts.add("Susan Someone");
		involvedContacts.add("Steve Aoki");
		involvedContacts.add("Armand Maree");
		involvedContacts.add("Arno Grobler");
		String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
		String[] data = {"Horse photo", "Hey Acuben, here is the photo you wanted."};
		long time = System.currentTimeMillis();
		RawData rawData = new RawData(pimSource, userId, involvedContacts, pimItemId, data, time);
		String[] topics = {"horse", "photo"};
		ProcessedData processedData = new ProcessedData(null, rawData.getPimSource(), rawData.getUserId(), involvedContacts.toArray(new String[0]), rawData.getPimItemId(), topics, rawData.getTime());

		Assert.assertEquals("Failure - pimSource differs.", rawData.getPimSource(), processedData.getPimSource());
		Assert.assertEquals("Failure - userId differs.", rawData.getUserId(), processedData.getUserId());
		Assert.assertEquals("Failure - involvedContacts length differs.", rawData.getInvolvedContacts().size(), processedData.getInvolvedContacts().length);

		for (int i = 0; i < rawData.getInvolvedContacts().size(); i++)
			Assert.assertEquals("Failure - involvedContacts[" + i + "] differs.", rawData.getInvolvedContacts().get(i), processedData.getInvolvedContacts()[i]);

		Assert.assertEquals("Failure - pimItemId differs.", rawData.getPimItemId(), processedData.getPimItemId());
		Assert.assertEquals("Failure - topics length differs.", topics.length, processedData.getTopics().length);

		for (int i = 0; i < topics.length; i++)
			Assert.assertEquals("Failure - topics[" + i + "] differs.", topics[i], processedData.getTopics()[i]);

		Assert.assertEquals("Failure - time differs.", rawData.getTime(), processedData.getTime());
	}
}
