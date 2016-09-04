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
* @since   1.0.0
*/
public class ProcessedDataListener {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PimProcessedDataRepository processedDataRepository;

	@Autowired
	private TopicRepository topicRepository;

	/**
	* A queue that all ProcessedDataListeners share that temporarily stores low priority {@link listeners.PendingTopics} while they wait to be inserted into the database.
	*/
	private static LinkedBlockingQueue<PendingTopic> pendingTopics = new LinkedBlockingQueue<>();

	/**
	* A queue that all ProcessedDataListeners share that temporarily stores high priority {@link listeners.PendingTopics} while they wait to be inserted into the database.
	*/
	private static LinkedBlockingQueue<PendingTopic> priorityPendingTopics = new LinkedBlockingQueue<>();

	/**
	* A single thread shared by all ProcessedDataListeners that will add {@link listeners.PendingTopic} objects to the database.
	*/
	private static Thread addToDatabaseThread = null;

	/**
	* Indicates whether the addToDatabaseThread should stop processing.
	*/
	private static boolean stop = false;

	/**
	* Default constructor.
	* <p>
	*	This will start the addToDatabaseThread automatically.
	* </p>
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
					// System.out.println("Persisting: " + pendingTopic);
					Topic topicInRepo = topicRepository.findByTopicAndUserId(pendingTopic.getTopic(), processedData.getUserId());

					if (topicInRepo == null) { // topic not in db yet
						List<String> processedDataIds = new ArrayList<>();
						processedDataIds.add(processedData.getId()); // ids of all topics containing the current topic (only one in this case).
						topicInRepo = new Topic(processedData.getUserId(), pendingTopic.getTopic(), pendingTopic.getRemainingTopics(), processedDataIds, processedData.getTime());

						if (pendingTopic.isPerson() && !topicInRepo.isPerson())
							topicInRepo.setIsPerson(true);

						topicRepository.save(topicInRepo); // persist new topic
						topicInRepo = topicRepository.findByTopicAndUserId(topicInRepo.getTopic(), topicInRepo.getUserId());
						// System.out.println("Added topic: " + topicInRepo.getTopic() + "  for user: " + userRepository.findByUserId(processedData.getUserId()).getGmailId());
					}
					else {
						topicInRepo.addRelatedTopics(pendingTopic.getRemainingTopics());
						topicInRepo.addProcessedDataId(processedData.getId());
						topicInRepo.setTime(processedData.getTime());

						if (pendingTopic.isPerson() && !topicInRepo.isPerson())
							topicInRepo.setIsPerson(true);

						topicRepository.save(topicInRepo);
						// System.out.println("Updated topic: " + topicInRepo.getTopic() + "  for user: " + userRepository.findByUserId(processedData.getUserId()).getGmailId());
					}
				}
			}
		};

		addToDatabaseThread.start();
	}

	/**
	* Receives low priority processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receiveProcessedData(ProcessedData processedData) throws InterruptedException {
		// System.out.println("Received processedData for user: " + processedData.getUserId());
		List<PendingTopic> pt = processProcessedData(processedData);

		for (PendingTopic pendingTopic : pt)
			pendingTopics.put(pendingTopic);
	}

	/**
	* Receives high priority processedData and updates the userId then sends the object to the repositry for persistence.
	* @param processedData The object that needs to be persisted.
	*/
	public void receivePriorityProcessedData(ProcessedData processedData) throws InterruptedException {
		// System.out.println("Received priorityProcessedData for user: " + processedData.getUserId());
		List<PendingTopic> pt = processProcessedData(processedData);

		for (PendingTopic pendingTopic : pt)
			priorityPendingTopics.put(pendingTopic);
	}

	/**
	* Will break the processedData's topics into smaller topics and create PendingTopics with these smaller topics.
	* @param processedData The {@link data.ProcessedData} object that has to be processed.
	* @return A list of smaller topics processed and contained in a {@link java.util.List} of {@link listeners.PendingTopic}.
	*/
	public List<PendingTopic> processProcessedData(ProcessedData processedData) {
		List<PendingTopic> pt = new ArrayList<>();

		try {
			User user = userRepository.findByPimId(processedData.getPimSource(), processedData.getUserId());
			processedData.setUserId(user.getUserId());

			if (user == null) // no user exists with this gmail id
				return pt;

			List<String> cleanedTopics = new ArrayList<>();

			for (String t : processedData.getTopics()) {
				if (!t.contains(user.getFirstName()) && !t.contains(user.getLastName()))
					cleanedTopics.add(t);
			}

			processedData.setTopics(cleanedTopics.toArray(new String[0]));
			processedDataRepository.save(processedData); // persist data
			processedData = processedDataRepository.findByPimSourceAndPimItemId(processedData.getPimSource(), processedData.getPimItemId());

			if (processedData.getTopics() != null)
				for (String topic : processedData.getTopics()) { // iterate all topics in data
					if (topic.contains(user.getFirstName()) || topic.contains(user.getLastName()))
						continue;

					ArrayList<String> remainingTopics = new ArrayList<>(); // all topics excluding the current one

					for (String t : processedData.getTopics()) {
						if (!t.equals(topic))
							remainingTopics.add(t);
					}

					if (processedData.getInvolvedContacts() != null)
						for (String contact : processedData.getInvolvedContacts())
							remainingTopics.add(contact);

					pt.add(new PendingTopic(topic, processedData, remainingTopics));
				}

			if (processedData.getInvolvedContacts() != null)
				for (String contact : processedData.getInvolvedContacts()) { // iterate all topics in data
					if (contact.contains(user.getFirstName()) || contact.contains(user.getLastName()))
						continue;

					ArrayList<String> remainingTopics = new ArrayList<>(); // all contacts excluding the current one

					for (String t : processedData.getInvolvedContacts()) {
						if (!t.equals(contact))
							remainingTopics.add(t);
					}

					if (processedData.getTopics() != null)
						for (String topic : processedData.getTopics())
							remainingTopics.add(topic);

					PendingTopic pendingTopic = new PendingTopic(contact, processedData, remainingTopics);
					pendingTopic.setIsPerson(true);
					pt.add(pendingTopic);
				}
		}
		catch (Exception e) { // never crash thread
			e.printStackTrace();
		}

		return pt;
	}
}
