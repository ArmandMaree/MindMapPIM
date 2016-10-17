package listeners;

import data.ImageDetails;
import data.PimId;
import data.ProcessedData;
import data.Topic;
import data.TopicRequest;
import data.TopicResponse;
import data.User;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repositories.ImageDetailsRepository;
import repositories.PimProcessedDataRepository;
import repositories.TopicRepository;
import repositories.UserRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Receives requests from a queue messaging service that requires some operation with topics.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class TopicListener {
	private final String topicResponseQueueName = "topic-response.frontend.rabbit";

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private ImageDetailsRepository imageRepository;

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
	*	The most relevant topic will always be added to the returnTopics list. It then looks at the remaining topics and if the next topic does not appear in any of the related topics of the topics in the returnTopics list then the current topic will be added to the returnTopic list. This allows a more diverse selection of topics.<br>
	*	The contacts are selected in the same manner.
	* </p>
	* @param topicRequest Request for topics dequeued form messaging service.
	*/
	public void receiveTopicRequest(TopicRequest topicRequest) throws NoSuchTopicException {
		// finds the topics related to the node specified in path
		System.out.println("RECEIVED: " + topicRequest);
		Topic lastTopicInPath = null;
		List<List<Topic>> topicsAndContacts = null;

		try {
			lastTopicInPath = findTopicAtEndOfPath(topicRequest.getPath(), topicRequest.getUserId());
			// topics that need to be returned.
			topicsAndContacts = getRelatedTopics(lastTopicInPath, topicRequest.getMaxNumberOfTopics(), topicRequest.getUserId(), topicRequest.getExclude()); 
		}
		catch (NoSuchTopicException nste) {
			nste.printStackTrace();
			String[] emptyArray = new String[0];
			String[][][] emptyArray3D = new String[0][0][0];
			TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), new ImageDetails[0], emptyArray, emptyArray3D); // create topic response without topics objects
			System.out.println("Respond from topicListener: " + topicResponse);
			rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
			return;
		}

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

		int involvedContactsSize = Math.min(((topicRequest.getPath() == null || topicRequest.getPath().length == 0 || topicRequest.getPath()[0].equals("")) ? topicRequest.getMaxNumberOfTopics() : 2), topicsAndContacts.get(1).size());
		String[] involvedContacts = new String[involvedContactsSize];

		// gets the text of of the contacts that will be returned
		for(int i = 0; i < involvedContactsSize; i++)
			involvedContacts[i] = topicsAndContacts.get(1).get(i).getTopic();

		ImageDetails[] imageDetailsArr = getImageDetails(topicsText);

		TopicResponse topicResponse = new TopicResponse(topicRequest.getUserId(), imageDetailsArr, involvedContacts, nodesArr); // create topic response without topics objects
		rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse); // send topic response to queue
		System.out.println("Respond from topicListener: " + topicResponse);
	}

	/**
	* Converts a 3D {@link java.util.list} of {@link java.lang.String}s to a 3D {@link java.lang.String} array.
	* @param list The list that has to be converted.
	* @return The converted array.
	*/
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

	/**
	* Finds the last topic in the provided path.
	* <p>
	*	The path is traversed and at each index it is checked whether the user provided has a topic with the same name stored in the database. It also checks whether the topic has a related topic that has the same name as the topic name specified in the next index.<br>
	*	If the path is null, an empty array, or the first element is an empty string then null will be returned.
	* </p>
	* @param path The path that has to be followed to find the last node.
	* @param userId The user to which this path applies.
	* @return The topic at the end of the path or null if the path is null, an empty array, or the first element is an empty string.
	* @throws NoSuchTopicException If at any point during the traversal a node is found that either does not exist for the given user or it is not related to the topic in the previous index.
	*/
	public Topic findTopicAtEndOfPath(String[] path, String userId) throws NoSuchTopicException {
		if (path == null || path.length == 0 || path[0].equals(""))
			return null;

		Topic returnTopic = null;

		for (int i = 0; i < path.length; i++) { // iterate all nodes in path
			Topic topic = topicRepository.findByTopicAndUserIdAndHidden(path[i], userId, false); // find topic for specified user

			if (topic == null) // user does not have a topic with this name
				throw new NoSuchTopicException(path[i]);


			if (i == path.length - 1) { // if on last node of path
				returnTopic = topic;
			}
			else { 
				List<Topic> relatedTopics = new ArrayList<>(); // store the related topics of the topic retrieved from repo

				for (String t : topic.getRelatedTopics()) {
					Topic relatedTopicInRepo = topicRepository.findByTopicAndUserIdAndHidden(t, userId, false);

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

	/**
	* Gets the topics and contacts related to the provided topic.
	* <p>
	*	The topics that will be returned will be sorted in decending order based on {@link data.Topic#getWeight} method.
	* </p>
	* @param topic The topic for which the related topics has to be found.
	* @param maxNumberOfTopics The maximum number of topics and contacts that will be returned.
	* @param userId The user for which the topics must be retrieved.
	* @return A 2D {@link java.util.List} that contains 2 {@link java.util.List}. The first is the related topics and the second is the contacts.
	*/
	public List<List<Topic>> getRelatedTopics(Topic topic, int maxNumberOfTopics, String userId, String[] excludeList) {
		List<Topic> topics;

		if (topic == null) {
			topics = topicRepository.findByUserIdAndHidden(userId, false);
		}
		else {
			topics = new ArrayList<>();

			for (String t : topic.getRelatedTopics()) {
				Topic relatedTopicInRepo = topicRepository.findByTopicAndUserIdAndHidden(t, userId, false);

				if (relatedTopicInRepo != null)
					topics.add(relatedTopicInRepo);
			}
		}

		Collections.sort(topics, Collections.reverseOrder()); // sort according to weight descending
		List<Topic> returnTopics = new ArrayList<>();
		List<Topic> returnContacts = new ArrayList<Topic>();

		// extracts the contacts and most relevant topics until both have topicRequest.getMaxNumberOfTopics() or there are no more topics
		while (topics.size() > 0) {
			if (!checkExcludeList(excludeList, topics.get(0))) {
				if (topics.get(0).getPerson() && returnContacts.size() < maxNumberOfTopics)
					returnContacts.add(topics.remove(0));
				else if(!topics.get(0).getPerson() && returnTopics.size() < maxNumberOfTopics)
					returnTopics.add(topics.remove(0));
				else
					topics.remove(0);

				if(returnContacts.size() >= maxNumberOfTopics && returnTopics.size() >= maxNumberOfTopics)
					break;
			}
			else
				topics.remove(0);
		}

		List<List<Topic>> topicsAndContacts = new ArrayList<>();
		topicsAndContacts.add(returnTopics);
		topicsAndContacts.add(returnContacts);
		return topicsAndContacts;
	}

	public boolean checkExcludeList(String[] excludeList, Topic topic) {
		if (excludeList == null)
			return false;
		
		for (String ex : excludeList)
			if (ex.equals(topic.getTopic()))
				return true;

		return false;
	}

	/**
	* Receives a topic that must be updated.
	* <p>
	*	It updates the topic in the database that has the same {@link data.Topic#topic} as the one in the parameter. Currently the fields that can be updated are:
	*	<ul>
	*		<li>{@link data.Topic#person}</li>
	*		<li>{@link data.Topic#hidden}</li>
	*	</ul>
	* </p>
	* @param topic The topic that contains the updated information.
	*/
	public void receiveTopicUpdate(Topic topic) {
		System.out.println("Received for modify: " + topic);
		Topic topicInRepo = topicRepository.findByTopicAndUserId(topic.getTopic(), topic.getUserId());

		if (topicInRepo == null)
			System.out.println("No topic found with name " + topic.getTopic());
		else {
			if (topic.getPerson() != topicInRepo.getPerson())
				topicInRepo.setPerson(topic.getPerson());

			if (topic.getHidden() != topicInRepo.getHidden())
				topicInRepo.setHidden(topic.getHidden());

			topicRepository.save(topicInRepo);
			System.out.println("TOPIC AFTER MODIFICATION: " + topicInRepo);
		}
	}

	public ImageDetails[] getImageDetails(String[] topics) {
		ImageDetails[] imageDetailsArr = new ImageDetails[topics.length];

		for (int i = 0; i < topics.length; i++) {
			String topic = topics[i];
			ImageDetails imageDetails = imageRepository.findByTopic(topic);

			if (imageDetails == null)
				imageDetails = new ImageDetails(topic);

			imageDetailsArr[i] = imageDetails;
		}

		return imageDetailsArr;
	}
}
