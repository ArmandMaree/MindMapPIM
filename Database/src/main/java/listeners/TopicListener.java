package listeners;

import java.util.*;

import repositories.*;
import data.Topic;

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
		System.out.println("Database received: " + topicRequest);
		String[] returnTopics = null; // topics that need to be returned.

		if (topicRequest.getPath() == null || topicRequest.getPath().length == 0) { // if a path is not specified
			System.out.println("Path 0 or null");
			List<Topic> topics = topicRepository.findByUserId(topicRequest.getUserId()); // get all topics from repo of this user
			System.out.println("List topics size: " + topics.size());
			returnTopics = new String[topics.size()];
			System.out.println("Return size 1: " + returnTopics.length);

			for (int i = 0; i < topics.size(); i++) { // store the related topics for later 
				returnTopics[i] = topics.get(i).getTopic();

				// System.out.println("returnTopics["+i+"]: " + returnTopics.length);
			}
		}
		else { // a path is specified
			for (int i = 0; i < topicRequest.getPath().length; i++) { // iterate all nodes in path
				String pathTopic = topicRequest.getPath()[i]; // current node in path
				Topic topic = topicRepository.findByTopicAndUserId(pathTopic, topicRequest.getUserId()); // find topic for specified user

				if (topic == null) // user does not have a topic with this name
					break;

				ArrayList<String> relatedTopics = new ArrayList<>(Arrays.asList(topic.getRelatedTopics())); // store the related topics of the topic retrieved from repo

				if (i == topicRequest.getPath().length - 1) { // if on last node of path
					if (topicRequest.getExclude() != null && topicRequest.getExclude().length != 0) // if exclude contains nodes, then remove them from related topics
						relatedTopics.removeAll(Arrays.asList(topicRequest.getExclude()));

					relatedTopics.removeAll(Arrays.asList(topicRequest.getPath())); // remove all topics that occur in path

					if (relatedTopics.size() != 0) // related topics not empty then convert to array
						returnTopics = relatedTopics.toArray(new String[0]);

					break;
				}
				else if (!relatedTopics.contains(topicRequest.getPath()[i + 1])) // else if related topics does not contain next node in path then stop
					break;
			}
		}

		if (returnTopics == null){ // no related topics exist for the given path
			rabbitTemplate.convertAndSend(topicResponseQueueName, new TopicResponse(topicRequest.getUserId(), null, null)); // send topic response that contains no topics
			return;
		}

		List<Topic> topicsObjectReturn = new ArrayList<>(); // Topic objects for the related topics

		for (String t : returnTopics) // get the topic objects for each related topic from the repo and store them
			topicsObjectReturn.add(topicRepository.findByTopicAndUserId(t, topicRequest.getUserId()));

		Collections.sort(topicsObjectReturn); // sort according to weight

		if (topicsObjectReturn.size() > topicRequest.getMaxNumberOfTopics()) // chop off excess topics
			topicsObjectReturn = topicsObjectReturn.subList(0, topicRequest.getMaxNumberOfTopics());

		returnTopics = new String[topicsObjectReturn.size()];

		for (int i = 0; i < topicsObjectReturn.size(); i++) // store related topics as simple text
			returnTopics[i] = topicsObjectReturn.get(i).getTopic();

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), returnTopics, topicsObjectReturn.toArray(new Topic[0])); // create topic response
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse); // send topic response to queue
		System.out.println("Database responds: " + topicResponse);
	}
}
