package testers.listeners.frontend;

import testers.AbstractTester;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
* Unit test methods for TopicRequest.
*
* @author Armand Maree
* @since 2016-07-21
*/
public class TopicRequestTester extends AbstractTester {
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
	public void test() {
		Assert.assertNotNull("Failure - this is null.", this); // this is just a debugging unit test
	}
}