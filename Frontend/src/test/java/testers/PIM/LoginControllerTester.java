package testers.data;
import listeners.*;
import data.*;
import PIM.*;
import PIM.LoginController;
import testers.AbstractTester;
import org.springframework.beans.factory.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
* Unit test methods for the RawData.
*
* @author Arno Grobler
* @since 2016-07-25
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PIM.Application.class)
public class LoginControllerTester extends AbstractTester {
	private boolean setUpDone = false;

	@Autowired
	private LoginController loginController;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
	LinkedBlockingQueue<TopicResponse> topicResponseLL;

	@Autowired
	LinkedBlockingQueue<UserIdentified> userResponseLL;

	@Autowired
	LinkedBlockingQueue<UserIdentified> userCheckResponseLL;

	// @Autowired
	// LinkedBlockingQueue<UserIdentified> TopicResponse;

	// public TopicResponse(String userId, String[] topicsText, Topic[] topics) {
	// 	this.userId = userId;
	// 	this.topicsText = topicsText;
	// 	this.topics = topics;
	// }
	@Before
	public void setUp() {
		if (!setUpDone) {
			String[] mockrelated = {"Arno Grobler","Riding"};
			String[] mockprocessedDataIds = {"09865","098765"};
			String[] mockTopicText = {"Horse","Amy Lochner","Racing"};
			String[] involvedContacts = {"Armand Maree", "Arno Grobler"};
			String[][][] mockItemIds = null;
			TopicResponse tro = new TopicResponse("123456",mockTopicText, involvedContacts,mockItemIds);
			UserIdentified ui = new UserIdentified("123456",true, "Acuban","Cos","acubancos@gmail.com");
			try{
				topicResponseLL.put(tro);
				userCheckResponseLL.put(ui);
			}catch(Exception e){

			}
			setUpDone = true;
		}
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
	public void testLoginController() {
		Assert.assertNotNull("Failure - loginController is null.", loginController);
	}
	@Test
	public void testRecieveRequest() throws Exception{
		TopicRequest tr = new  TopicRequest("123456",null,null,4);
		String[] mockrelated = {"Arno Grobler","Riding"};
		String[] mockprocessedDataIds = {"09865","098765"};
		String[] mockTopicText = {"Horse","Amy Lochner","Racing"};
		String[] involvedContacts = {"Armand Maree", "Arno Grobler"};
		String[][][] mockItemIds = null;
		TopicResponse tro = new TopicResponse("123456",mockTopicText, involvedContacts,mockItemIds);

		Assert.assertEquals("Topic responses userId does not match",tro.getUserId(),loginController.recieveRequest(tr).getUserId());
		Assert.assertEquals("Topic responses topicsText do not match",tro.getTopicsText(),loginController.recieveRequest(tr).getTopicsText());

	}
	@Test
	public void testuserchecktest() throws Exception{
		Assert.assertEquals("Topic responses userId does not match",true,loginController.userchecktest(new User("Acuban","Cos","acubancos@gmail.com")).getIsRegistered());
	}
	// @Test
	// public void testSendNewDataSources() throws Exception
	// {
		
	// }
}
