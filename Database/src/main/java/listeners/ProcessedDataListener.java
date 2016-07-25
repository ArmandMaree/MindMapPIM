package listeners;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import data.*;
import repositories.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Receives processed data from a queue messaging applicatiion and persists it.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public class ProcessedDataListener {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	/**
	* Default constructor.
	*/
	public ProcessedDataListener() {

	}

	/**
	* Receives processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receiveProcessedData(ProcessedData processedData) {
		try {
			switch (processedData.getPimSource()) {
				case "Gmail": // data comes from gmail
					User user = userRepository.findByGmailId(processedData.getUserId());

					if (user == null) // no user exists with this gmail id
						return;

					processedData.setUserId(user.getUserId());
					break;
				default: // don't know where the data comes from.
					return;
			}


			processedData = processedDataRepository.save(processedData); // persist data

			for (String topic : processedData.getTopics()) { // iterate all topics in data
				ArrayList<String> remainingTopics = new ArrayList<>(); // all topics excluding the current one

				for (String t : processedData.getTopics()) {
					if (!t.equals(topic))
						remainingTopics.add(t);
				}

				synchronized(this) {
					Topic topicFromRepo = topicRepository.findByTopicAndUserId(topic, processedData.getUserId()); // gets the current topic from repo if it exists

					if (topicFromRepo == null) { // topic not in repo
						String[] processedDataIds = {processedData.getId()}; // ids of all topics containing the current topic (only one in this case).
						Topic t = new Topic(processedData.getUserId(), topic, remainingTopics.toArray(new String[0]), processedDataIds, System.currentTimeMillis());
						topicRepository.save(t); // persist new topic
					}
					else { // topic in repo
						ArrayList<String> repoTopics = new ArrayList<>(Arrays.asList(topicFromRepo.getRelatedTopics())); // get the related topics of the topic in the repo

						for (String t : remainingTopics) { // adds the topic in the repo's related topics to the related topic list
							if (!repoTopics.contains(t))
								repoTopics.add(t);
						}

						ArrayList<String> repoPdIds = new ArrayList<>(Arrays.asList(topicFromRepo.getProcessedDataIds())); // get the ids of the previous data that contains this topic
						repoPdIds.add(processedData.getId()); // add current data's id
						topicFromRepo.setRelatedTopics(repoTopics.toArray(new String[0])); // update related topics
						topicFromRepo.setProcessedDataIds(repoPdIds.toArray(new String[0])); // update processed data ids
						topicFromRepo.setTime(System.currentTimeMillis()); // update modified time to current time
						topicRepository.save(topicFromRepo); // update topic in repo
					}
				}
			}
		}
		catch (Exception e) { // never crash app
			e.printStackTrace();
		}
	}
}
