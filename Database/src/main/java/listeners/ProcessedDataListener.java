package listeners;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import data.ProcessedData;
import repositories.pimprocesseddata.PimProcessedDataRepository;
import repositories.user.*;
import repositories.topic.*;

/**
* Receives processed data from a queue messaging applicatiion and persists it.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public class ProcessedDataListener {
	private PimProcessedDataRepository processedDataRepository;
	private UserRepository userRepository;
	private TopicRepository topicRepository;

	/**
	* Default constructor and initializes some variables.
	* @param processedDataRepository The repository where the processed data will be persisted.
	* @param userRepository The repository where user information is persisted.
	*/
	public ProcessedDataListener(UserRepository userRepository, PimProcessedDataRepository processedDataRepository, TopicRepository topicRepository) {
		this.userRepository = userRepository;
		this.processedDataRepository = processedDataRepository;
		this.topicRepository = topicRepository;
	}

	/**
	* Receives processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receiveProcessedData(ProcessedData processedData) {
		try {
			switch (processedData.getPimSource()) {
				case "Gmail":
					User user = userRepository.findByGmailId(processedData.getUserId());

					if (user == null)
						return;

					processedData.setUserId(user.getUserId());
					break;
				default:
					break;
			}


			ProcessedData pd = processedDataRepository.save(processedData);
			String[] pdId = {pd.getId()};
			ArrayList<Topic> topics = new ArrayList<>();

			for (String topic : pd.getTopics()) {
				ArrayList<String> remainingTopics = new ArrayList<>();

				for (String t : pd.getTopics()) {
					if (!t.equals(topic))
						remainingTopics.add(t);
				}

				synchronized(this) {
					Topic topicFromRepo = topicRepository.findByTopicAndUserId(topic, pd.getUserId());

					if (topicFromRepo == null) {
						Topic t = new Topic(pd.getUserId(), topic, remainingTopics.toArray(new String[0]), pdId, System.currentTimeMillis());
						topicRepository.save(t);
					}
					else {
						ArrayList<String> repoTopics = new ArrayList<>(Arrays.asList(topicFromRepo.getRelatedTopics()));

						for (String t : remainingTopics) {
							if (!repoTopics.contains(t))
								repoTopics.add(t);
						}

						ArrayList<String> repoPdIds = new ArrayList<>(Arrays.asList(topicFromRepo.getProcessedDataIds()));
						repoPdIds.add(pd.getId());
						topicFromRepo.setRelatedTopics(repoTopics.toArray(new String[0]));
						topicFromRepo.setProcessedDataIds(repoPdIds.toArray(new String[0]));
						topicFromRepo.setTime(System.currentTimeMillis());
						topicRepository.save(topicFromRepo);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
