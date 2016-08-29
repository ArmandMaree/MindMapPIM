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
* @since   1.0.0
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
	* 
	* <p>
	*	<ul>
	*		<li>To request topics for the center node of the MindMap specify an empty path for the {@link data.TopicRequest}. This will return {@link data.TopicRequest#getMaxNumberOfTopics} topics and {@link data.TopicRequest#getMaxNumberOfTopics} contacts if there are so many available.</li>
	*		<li>To request topics for another node in the MindMap specify the path from me (excluded) to the node (included) as the path for the {@link data.TopicRequest}. This will return {@link data.TopicRequest#getMaxNumberOfTopics} topics and 2 contacts if there are so many available.</li>
	*	</ul>
	*	<br>
	*	How the topic selection algorithm works:<br>
	*	The most relevant topic will always be added to the returnTopics list. It then looks at the remaining topics and if the next topic does not appear in any of the related topics of the topics in the returnTopics list then the current topic will be added to the returnTopic list. This allows a more diverse selection of topics.
	* </p>
	* @param topicRequest Request for topics dequeued form messaging application.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) {
		System.out.println("Received: " + topicRequest);
		List<Topic> topics = new ArrayList<>(); // topics that need to be returned.

		// finds the topics related to the node specified in path
		if (topicRequest.getPath() == null || topicRequest.getPath().length == 0 || topicRequest.getPath()[0].equals("")) { // if a path is not specified
			topics = topicRepository.findByUserId(topicRequest.getUserId()); // get all topics from repo of this user
			List<String> excludeList = Arrays.asList(topicRequest.getExclude());

			for (int i = 0; i < topics.size(); i++)
				if (excludeList.contains(topics.get(i).getTopic())) {
					topics.remove(i);
					i--;
				}
		}
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
					for (String relatedTopic : relatedTopics) {
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

		Collections.sort(topics, Collections.reverseOrder()); // sort according to weight descending
		List<Topic> returnTopics = new ArrayList<>();
		List<Topic> returnContacts = new ArrayList<Topic>();

		// extracts the contacts and most relevant topics until both have topicRequest.getMaxNumberOfTopics() or there are no more topics
		if (topics.size() <= topicRequest.getMaxNumberOfTopics()) {
			for (int i = 0; i < topics.size(); i++) {
				while (topics.size() > i && topics.get(i).isPerson())
					returnContacts.add(topics.remove(i));
			}

			returnTopics = topics;
		}
		else {
			while (topics.size() > 0 && topics.get(0).isPerson())
				returnContacts.add(topics.remove(0));

			if (topics.size() != 0) {
				returnTopics.add(topics.get(0));

				for (int i = 1; i < topics.size() && (returnTopics.size() < topicRequest.getMaxNumberOfTopics() || returnContacts.size() < topicRequest.getMaxNumberOfTopics()); i++) { // take the most relevant topics but also try to reduce closely related topics
					while (returnContacts.size() < topicRequest.getMaxNumberOfTopics() && topics.size() > i && topics.get(i).isPerson()) {
						returnContacts.add(topics.remove(i));
						// System.out.println("Topic[" + i + "]: " + topics.get(i).getTopic() + " is a person.");
					}

					if (topics.size() > i && returnTopics.size() < topicRequest.getMaxNumberOfTopics() && !topics.get(i).isPerson()) {
						// System.out.println("Topic[" + i + "]: " + topics.get(i).getTopic() + " is not a person.");
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
			}
		}

		// PIM IDs of each of the topics that will be returned
		List<String[][]> nodes = new ArrayList<>();

		for (Topic topic : returnTopics) {
			List<String[]> pims  = new ArrayList<>();
			List<String> gmailIds  = new ArrayList<>();

			for (String processedDataId : topic.getProcessedDataIds()) {
				ProcessedData pd = processedDataRepository.findById(processedDataId);

				if (pd != null)
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

		// PIM IDs of each of the contacts that will be returned
		for (Topic topic : returnContacts) {
			List<String[]> pims  = new ArrayList<>();
			List<String> gmailIds  = new ArrayList<>();

			for (String processedDataId : topic.getProcessedDataIds()) {
				ProcessedData pd = processedDataRepository.findById(processedDataId);

				if (pd != null)
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

		// gets the text of the topics to be returned
		for (int i = 0; i < returnTopics.size(); i++)
			topicsText[i] = returnTopics.get(i).getTopic();

		int involvedContactsSize = Math.min(((topicRequest.getPath() == null || topicRequest.getPath().length == 0|| topicRequest.getPath()[0].equals("")) ? topicRequest.getMaxNumberOfTopics() : 2), returnContacts.size());
		String[] involvedContacts = new String[involvedContactsSize];

		// gets the text of of the contacts that will be returned
		for(int i = 0; i < involvedContactsSize; i++)
			involvedContacts[i] = returnContacts.get(i).getTopic();

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), topicsText, involvedContacts, nodesArr); // create topic response without topics objects
		System.out.println("Respond: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse); // send topic response to queue
	}
}
