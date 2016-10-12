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
	public void receiveTopicRequest(TopicRequest topicRequest) throws NoSuchTopicException {
		// finds the topics related to the node specified in path
		System.out.println("RECEIVED: " + topicRequest);
		Topic lastTopicInPath = null;
		lastTopicInPath = findTopicAtEndOfPath(topicRequest.getPath(), topicRequest.getUserId());
		// topics that need to be returned.
		List<List<Topic>> topicsAndContacts = getRelatedTopics(lastTopicInPath, topicRequest.getMaxNumberOfTopics(), topicRequest.getUserId()); 


		// PIM IDs of each of the topics that will be returned
		List<List<List<String>>> nodes = new ArrayList<>();
		User user = userRepository.findByUserId(topicRequest.getUserId());

		for (Topic topic : topicsAndContacts.get(0)) {
			List<List<String>> nodeIds = new ArrayList<>();

			for (PimId pimId : user.getPimIds()) {
				List<String> ids = new ArrayList<>();
				ids.add(pimId.pim);
				nodeIds.add(ids);
			}

			for (String processedDataId : topic.getProcessedDataIds()) {
				ProcessedData pd = processedDataRepository.findById(processedDataId);
				
				for (int i = 0; i < nodeIds.size(); i++) {
					String pSource = nodeIds.get(i).get(0);

					if (pSource.equals(pd.getPimSource())) {
						nodeIds.get(i).add(pd.getPimItemId());
						break;
					}
				}
			}

			nodes.add(nodeIds);
		}

		// PIM IDs of each of the contacts that will be returned
		for (Topic topic : topicsAndContacts.get(1)) {
			List<List<String>> nodeIds = new ArrayList<>();

			for (PimId pimId : user.getPimIds()) {
				List<String> ids = new ArrayList<>();
				ids.add(pimId.pim);
				nodeIds.add(ids);
			}

			for (String processedDataId : topic.getProcessedDataIds()) {
				ProcessedData pd = processedDataRepository.findById(processedDataId);

				for (int i = 0; i < nodeIds.size(); i++) {
					String pSource = nodeIds.get(i).get(0);

					if (pSource.equals(pd.getPimSource())) {
						nodeIds.get(i).add(pd.getPimItemId());
						break;
					}
				}
			}

			nodes.add(nodeIds);
		}

		String[][][] nodesArr = listToArray3D(nodes);
		String[] topicsText = new String[topicsAndContacts.get(0).size()];

		// gets the text of the topics to be returned
		for (int i = 0; i < topicsAndContacts.get(0).size(); i++)
			topicsText[i] = topicsAndContacts.get(0).get(i).getTopic();

		int involvedContactsSize = Math.min(((topicRequest.getPath() == null || topicRequest.getPath().length == 0|| topicRequest.getPath()[0].equals("")) ? topicRequest.getMaxNumberOfTopics() : 2), topicsAndContacts.get(1).size());
		String[] involvedContacts = new String[involvedContactsSize];

		// gets the text of of the contacts that will be returned
		for(int i = 0; i < involvedContactsSize; i++)
			involvedContacts[i] = topicsAndContacts.get(1).get(i).getTopic();

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), topicsText, involvedContacts, nodesArr); // create topic response without topics objects
		System.out.println("Respond from topicListener: " + topicResponse);
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse); // send topic response to queue
	}

	private String[][][] listToArray3D(List<List<List<String>>> list) {
		List<String[][]> tmp3d = new ArrayList<>();

		for (List<List<String>> d2 : list) {
			List<String[]> tmp2d = new ArrayList<>();

			for (List<String> d1 : d2)
				tmp2d.add(d1.toArray(new String[0]));
		
			tmp3d.add(tmp2d.toArray(new String[0][0]));
		}

		return tmp3d.toArray(new String[0][0][0]);
	}

	public Topic findTopicAtEndOfPath(String[] path, String userId) throws NoSuchTopicException {
		if (path == null || path.length == 0 || path[0].equals(""))
			return null;

		Topic returnTopic = null;

		for (int i = 0; i < path.length; i++) { // iterate all nodes in path
			Topic topic = topicRepository.findByTopicAndUserId(path[i], userId); // find topic for specified user

			if (topic == null) // user does not have a topic with this name
				throw new NoSuchTopicException(path[i]);


			if (i == path.length - 1) { // if on last node of path
				returnTopic = topic;
			}
			else { 
				List<Topic> relatedTopics = new ArrayList<>(); // store the related topics of the topic retrieved from repo

				for (String t : topic.getRelatedTopics()) {
					Topic relatedTopicInRepo = topicRepository.findByTopicAndUserId(t, userId);

					if (relatedTopicInRepo != null)
						relatedTopics.add(relatedTopicInRepo);
				}

				boolean found = false;

				for (Topic t : relatedTopics) {
					if (t.equals(path[i + 1])) {
						found = true;
						break;
					}		
				}

				if (!found) // else if related topics does not contain next node in path then stop
					throw new NoSuchTopicException(path[i] + "->" + path[i + 1]);
			}
		}

		return returnTopic;
	}

	public List<List<Topic>> getRelatedTopics(Topic topic, int maxNumberOfTopics, String userId) {
		List<Topic> topics;

		if (topic == null) {
			topics = topicRepository.findByUserId(userId);
		}
		else {
			topics = new ArrayList<>();

			for (String t : topic.getRelatedTopics()) {
				Topic relatedTopicInRepo = topicRepository.findByTopicAndUserId(t, userId);

				if (relatedTopicInRepo != null)
					topics.add(relatedTopicInRepo);
			}
		}

		Collections.sort(topics, Collections.reverseOrder()); // sort according to weight descending
		List<Topic> returnTopics = new ArrayList<>();
		List<Topic> returnContacts = new ArrayList<Topic>();

		// extracts the contacts and most relevant topics until both have topicRequest.getMaxNumberOfTopics() or there are no more topics
		if (topics.size() <= maxNumberOfTopics) {
			for (int i = 0; i < topics.size(); i++) {
				while (topics.size() > i && topics.get(i).getPerson())
					returnContacts.add(topics.remove(i));
			}

			returnTopics = topics;
		}
		else {
			while (topics.size() > 0 && topics.get(0).getPerson())
				returnContacts.add(topics.remove(0));

			if (topics.size() != 0) {
				returnTopics.add(topics.get(0));

				for (int i = 1; i < topics.size() && (returnTopics.size() < maxNumberOfTopics || returnContacts.size() < maxNumberOfTopics); i++) { // take the most relevant topics but also try to reduce closely related topics
					while (topics.size() > i && topics.get(i).getPerson()) {
						if (returnContacts.size() < maxNumberOfTopics)
							returnContacts.add(topics.remove(i));
						else
							topics.remove(i);
					}

					if (topics.size() > i && returnTopics.size() < maxNumberOfTopics) {
						boolean found = false;

						outerloop:
						for (Topic returnTopic : returnTopics) {
							for (String singleRelatedTopic : returnTopic.getRelatedTopics()) {
								if (singleRelatedTopic.equals(topics.get(i).getTopic())) {
									found = true;
									break outerloop;
								}
							}
						}

						if (!found)
							returnTopics.add(topics.get(i));
					}
				}
			}
		}

		List<List<Topic>> topicsAndContacts = new ArrayList<>();
		topicsAndContacts.add(returnTopics);
		topicsAndContacts.add(returnContacts);
		return topicsAndContacts;
	}

	public void receiveTopicUpdate(Topic topic) {
		System.out.println("Received: " + topic);
		Topic topicInRepo = topicRepository.findByTopicAndUserId(topic.getTopic(), topic.getUserId());

		if (topicInRepo == null)
			System.out.println("No topic found with name " + topic.getTopic());
		else {
			if (topic.getPerson() != topicInRepo.getPerson())
				topicInRepo.setPerson(topic.getPerson());

			if (topic.getHidden() != topicInRepo.getHidden())
				topicInRepo.setPerson(topic.getHidden());

			topicRepository.save(topicInRepo);
			System.out.println("TOPIC AFTER MODIFICATION: " + topicInRepo);
		}
	}
}
