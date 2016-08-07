package testers.repositories;

import repositories.*;
import data.*;
import testers.AbstractTester;

import java.util.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
* Unit test methods for ProcessedDataListener.
*
* @author Armand Maree
* @since 2016-07-24
*/
public class UserRepositoryTester extends AbstractTester {
	private boolean setUpDone = false;

	private final static String processedDataQueueName = "processed-data.database.rabbit";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Before
	public void setUp() {
		if (!setUpDone) {
			setUpDone = true;
		}

		userRepository.deleteAll();
	}

	@After
	public void tearDown() {
		// clean up after each test method
	}

	@Test
	public void testSaveAndRetrieve() throws InterruptedException {
		User acuben = new User("Acuben", "Cos", "acubencos@gmail.com");
		userRepository.save(acuben);
		User retrieve = userRepository.findByGmailId(acuben.getGmailId());
		Assert.assertNotNull("FAILURE - user not saved.", retrieve);
		Assert.assertNotNull("FAILURE - saved user has no ID.", retrieve.getUserId());
	}
}
