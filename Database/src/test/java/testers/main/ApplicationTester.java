package testers.processor;

import data.*;
import repositories.user.*;
import repositories.pimprocesseddata.*;
import testers.AbstractTester;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Unit test methods for the Application.
*
* @author Armand Maree
* @since 2016-07-20
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = main.Application.class)
public class ApplicationTester extends AbstractTester {
	private boolean setUpDone = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}

		userRepository.deleteAll();
		processedDataRepository.deleteAll();
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testRabbitTemplate() {
		Assert.assertNotNull("Failure - rabbitTemplate is null. Is RabbitMQ running?", rabbitTemplate);
	}

	@Test
	public void testUserRepository() {
		Assert.assertNotNull("Failure - userRepository is null. Is MongoDB running?", userRepository);
	}

	@Test
	public void testPimProcessedDataRepository() {
		Assert.assertNotNull("Failure - processedDataRepository is null. Is MongoDB running?", processedDataRepository);
	}

	@Test
	public void testUserSave() {
		User user = new User("Acuben", "Cos", "acubencos@gmail.com");
		User savedUser = userRepository.save(user);
		Assert.assertNotNull("Failure - savedUsers is null.", savedUser);
		Assert.assertEquals("Failure - firstName differs.", user.getFirstName(), savedUser.getFirstName());
		Assert.assertEquals("Failure - lastName differs.", user.getLastName(), savedUser.getLastName());
		Assert.assertEquals("Failure - gmailId differs.", user.getGmailId(), savedUser.getGmailId());
	}

	@Test
	public void testProcessedDataSave() {
		String pimSource = "Gmail";
		String userId = "acubencos@gmail.com";
		String[] involvedContacts = {"susan@gmail.com", "steve@gmail.com", "thabo@gmail.com", "precious@gmail.com"};
		String pimItemId = "f65465f46srg44s6r54t06s6s0df4t6dst0";
		String[] topics = {"horse", "photo"};
		long time = System.currentTimeMillis();
		ProcessedData processedData = new ProcessedData(pimSource, userId, involvedContacts, pimItemId, topics, time);

		ProcessedData pd = processedDataRepository.save(processedData);

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
