package listeners;

import java.util.*;

import repositories.*;
import data.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Receives topic requests from a queue messaging application and responds to it.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class TopicListener {
	private final String topicResponseQueueName = "topic-response.business.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	/**
	* Default constructor.
	*/
	public TopicListener() {

	}

	/**
	* Receives a request for new topics and adds the response to a queue.
	* @param topicRequest Request for topics dequeued form messaging application.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) {
		System.out.println("Received: " + topicRequest);
		List<Topic> topics = new ArrayList<>(); // topics that need to be returned.

		if (topicRequest.getPath() == null || topicRequest.getPath().length == 0 || topicRequest.getPath()[0].equals("")) // if a path is not specified
			topics = topicRepository.findByUserId(topicRequest.getUserId()); // get all topics from repo of this user
		else { // a path is specified
			for (int i = 0; i < topicRequest.getPath().length; i++) { // iterate all nodes in path
				String pathTopic = topicRequest.getPath()[i]; // current node in path
				Topic topic = topicRepository.findByTopicAndUserId(pathTopic, topicRequest.getUserId()); // find topic for specified user

				if (topic == null) // user does not have a topic with this name
					break;

				List<String> relatedTopics = topic.getRelatedTopics(); // store the related topics of the topic retrieved from repo

				if (i == topicRequest.getPath().length - 1) { // if on last node of path
					if (topicRequest.getExclude() != null && topicRequest.getExclude().length != 0) // if exclude contains nodes, then remove them from related topics
						relatedTopics.removeAll(Arrays.asList(topicRequest.getExclude()));

					relatedTopics.removeAll(Arrays.asList(topicRequest.getPath())); // remove all topics that occur in path
					System.out.println("Related topics: ");
					for (String relatedTopic : relatedTopics) {
						System.out.println("\t" + relatedTopic);
						Topic t = topicRepository.findByTopicAndUserId(relatedTopic, topicRequest.getUserId());

						if (t != null)
							topics.add(t);
					}
				}
				else if (!relatedTopics.contains(topicRequest.getPath()[i + 1])) // else if related topics does not contain next node in path then stop
					break;
			}
		}

		if (topics == null || topics.size() == 0) { // no related topics exist for the given path
			rabbitTemplate.convertAndSend(topicResponseQueueName, new TopicResponse(topicRequest.getUserId(), new String[0], null, null)); // send topic response that contains no topics
			System.out.println("No topics found for user: " + topicRequest.getUserId());
			return;
		}

		Collections.sort(topics, Collections.reverseOrder()); // sort according to weight
		List<Topic> returnTopics = new ArrayList<>();

		if (topics.size() <= topicRequest.getMaxNumberOfTopics()) {
			returnTopics = topics;
		}
		else {
			returnTopics.add(topics.get(0));

			for (int i = 1; i < topics.size() && returnTopics.size() < topicRequest.getMaxNumberOfTopics(); i++) { // take the most relevant topics but also try to reduce closely related topics
				boolean found = false;

				for (Topic returnTopic : returnTopics) {
					for (String singleRelatedTopic : returnTopic.getRelatedTopics()) {
						if (singleRelatedTopic.equals(topics.get(i).getTopic())) {
							found = true;
							break;
						}
					}

					if (found)
						break;
				}

				if (!found)
					returnTopics.add(topics.get(i));
			}
		}

		List<String[][]> nodes = new ArrayList<>();

		for (Topic topic : returnTopics) {
			List<String[]> pims  = new ArrayList<>();
			List<String> gmailIds  = new ArrayList<>();

			for (String processedDataId : topic.getProcessedDataIds()) {
				ProcessedData pd = processedDataRepository.findById(processedDataId);

				switch (pd.getPimSource()) {
					case "Gmail":
						gmailIds.add(pd.getPimItemId());
						break;
					default:
						break;
				}
			}

			pims.add(gmailIds.toArray(new String[0]));
			//facebook stuff here
			nodes.add(pims.toArray(new String[0][0]));
		}

		String[][][] nodesArr = nodes.toArray(new String[0][0][0]);
		String[] topicsText = new String[returnTopics.size()];

		for (int i = 0; i < returnTopics.size(); i++)
			topicsText[i] = returnTopics.get(i).getTopic();

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), topicsText, null, nodesArr); // create topic response without topics objects
		System.out.println("Respond: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse); // send topic response to queue
	}
}
