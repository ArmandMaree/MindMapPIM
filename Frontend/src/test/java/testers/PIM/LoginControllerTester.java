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
* @author Armand Maree
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
			Topic[] mockTopics = {new Topic("123456","Horse",mockrelated,mockprocessedDataIds,1000),new Topic("123456","Amy Lochner",mockrelated,mockprocessedDataIds,1000),new Topic("123456","Racing",mockrelated,mockprocessedDataIds,1000)};
			TopicResponse tro = new TopicResponse("123456",mockTopicText,mockTopics);
			try{
				topicResponseLL.put(tro);
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
		Topic[] mockTopics = {new Topic("123456","Horse",mockrelated,mockprocessedDataIds,1000),new Topic("123456","Amy Lochner",mockrelated,mockprocessedDataIds,1000),new Topic("123456","Racing",mockrelated,mockprocessedDataIds,1000)};
		TopicResponse tro = new TopicResponse("123456",mockTopicText,mockTopics);

		Assert.assertEquals("Topic responses userId does not match",tro.getUserId(),loginController.recieveRequest(tr).getUserId());
		Assert.assertEquals("Topic responses topicsText do not match",tro.getTopicsText(),loginController.recieveRequest(tr).getTopicsText());

	}
}
