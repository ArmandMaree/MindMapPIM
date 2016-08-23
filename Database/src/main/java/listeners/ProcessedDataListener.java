package listeners;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import data.*;
import repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

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

	private static LinkedBlockingQueue<PendingTopic> pendingTopics = new LinkedBlockingQueue<>();
	private static LinkedBlockingQueue<PendingTopic> priorityPendingTopics = new LinkedBlockingQueue<>();
	private static Thread addToDatabaseThread = null;
	private static boolean stop = false;

	/**
	* Default constructor.
	*/
	public ProcessedDataListener() {
		if (addToDatabaseThread != null)
			return;

		addToDatabaseThread = new Thread() {
			@Override
			public void run() {
				while (!stop) {
					PendingTopic pendingTopic;

					try {
						pendingTopic = priorityPendingTopics.poll();

						if (pendingTopic == null)
							pendingTopic = pendingTopics.poll(5, TimeUnit.SECONDS);

						if (pendingTopic == null) {
							Thread.sleep(5000);
							continue;
						}
					} catch(InterruptedException ignore) {
						continue;
					}

					ProcessedData processedData = pendingTopic.getProcessedData();
					Topic topicInRepo = topicRepository.findByTopicAndUserId(pendingTopic.getTopic(), processedData.getUserId());

					if (topicInRepo == null) { // topic not in db yet
						List<String> processedDataIds = new ArrayList<>();
						processedDataIds.add(processedData.getId()); // ids of all topics containing the current topic (only one in this case).
						topicInRepo = new Topic(processedData.getUserId(), pendingTopic.getTopic(), pendingTopic.getRemainingTopics(), processedDataIds, processedData.getTime());
						topicRepository.save(topicInRepo); // persist new topic
						topicInRepo = topicRepository.findByTopicAndUserId(topicInRepo.getTopic(), topicInRepo.getUserId());
						// System.out.println("Added topic: " + topicInRepo.getTopic() + "  for user: " + userRepository.findByUserId(processedData.getUserId()).getGmailId());
					}
					else {
						topicInRepo.addRelatedTopics(pendingTopic.getRemainingTopics());
						topicInRepo.addProcessedDataId(processedData.getId());
						topicInRepo.setTime(processedData.getTime());
						topicRepository.save(topicInRepo);
						// System.out.println("Updated topic: " + topicInRepo.getTopic() + "  for user: " + userRepository.findByUserId(processedData.getUserId()).getGmailId());
					}
				}
			}
		};

		addToDatabaseThread.start();
	}

	/**
	* Receives processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receiveProcessedData(ProcessedData processedData) throws InterruptedException {
		// System.out.println("Received processedData for user: " + processedData.getUserId());
		List<PendingTopic> pt = processProcessedData(processedData);

		for (PendingTopic pendingTopic : pt)	
			pendingTopics.put(pendingTopic);
	}

	/**
	* Receives processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receivePriorityProcessedData(ProcessedData processedData) throws InterruptedException {
		System.out.println("Received priorityProcessedData for user: " + processedData.getUserId());
		List<PendingTopic> pt = processProcessedData(processedData);

		for (PendingTopic pendingTopic : pt)
			priorityPendingTopics.put(pendingTopic);
	}

	public List<PendingTopic> processProcessedData(ProcessedData processedData) {
		List<PendingTopic> pt = new ArrayList<>();

		try {
			User user = null;

			switch (processedData.getPimSource()) {
				case "Gmail": // data comes from gmail
					user = userRepository.findByGmailId(processedData.getUserId());

					if (user == null) // no user exists with this gmail id
						return pt;

					processedData.setUserId(user.getUserId());
					break;
				default: // don't know where the data comes from.
					return pt;
			}

			List<String> cleanedTopics = new ArrayList<>();

			for (String t : processedData.getTopics()) {
				if (!t.contains(user.getFirstName()) && !t.contains(user.getLastName()))
					cleanedTopics.add(t);
			}

			processedData.setTopics(cleanedTopics.toArray(new String[0]));
			processedDataRepository.save(processedData); // persist data
			processedData = processedDataRepository.findByPimSourceAndPimItemId("Gmail", processedData.getPimItemId());

			for (String topic : processedData.getTopics()) { // iterate all topics in data
				if (topic.equals(user.getFirstName()) || topic.equals(user.getLastName()) || (topic.equals(user.getFirstName()) && topic.equals(user.getLastName())))
					continue;

				ArrayList<String> remainingTopics = new ArrayList<>(); // all topics excluding the current one

				for (String t : processedData.getTopics()) {
					if (!t.equals(topic))
						remainingTopics.add(t);
				}

				pt.add(new PendingTopic(topic, processedData, remainingTopics));
			}
		}
		catch (Exception e) { // never crash thread
			e.printStackTrace();
		}

		return pt;
	}
}
