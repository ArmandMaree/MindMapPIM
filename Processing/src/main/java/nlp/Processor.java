package nlp;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.*;

import data.*;
import nlp.NaturalLanguageProcessor;

/**
* Receives raw data and processes it with a NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class Processor implements Runnable {
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private CountDownLatch latch = new CountDownLatch(1);
	private String processedDataDatabaseQueueName = "processed-data.database.rabbit";

	private NaturalLanguageProcessor nlp;
	private RabbitTemplate rabbitTemplate;
	private LinkedBlockingQueue<RawData> rawDataQueue;
	private LinkedBlockingQueue<RawData> priorityRawDataQueue;

	/**
	* Default constructor.
	*/
	public Processor(LinkedBlockingQueue<RawData> rawDataQueue, LinkedBlockingQueue<RawData> priorityRawDataQueue, RabbitTemplate rabbitTemplate, NaturalLanguageProcessor nlp) {
		this.rawDataQueue = rawDataQueue;
		this.priorityRawDataQueue = priorityRawDataQueue;
		this.rabbitTemplate = rabbitTemplate;
		this.nlp = nlp;
	}

	public void run() {
		while (!stop) {
			try {
				boolean priority = false;
				RawData rawData = priorityRawDataQueue.poll();

				if (rawData == null)
					rawData = rawDataQueue.poll(10, TimeUnit.SECONDS);
				else
					priority = true;

				if (rawData == null)
					continue;

				ProcessedData processedData = process(rawData);

				if (processedData == null)
					return;

				pushToQueue(processedData, priority);
			}
			catch (Exception ignore) {}
		}
	}

	/**
	* Process rawData.
	* @param rawData The object that has to be processed.
	* @return The processed data.
	*/
	public ProcessedData process(RawData rawData) {
		List<String> topics = new ArrayList<>();
		ProcessedData processedData = null;

		if (nlp != null) {
			for (String part : rawData.getData()) {
				ArrayList<String> topicsIdentified = nlp.getTopics(part);

				for (String topic : topicsIdentified)
					topics.add(topic);
			}

			if (topics.size() == 0)
				return null;

			topics = nlp.purge(topics);

			List<String> people = new ArrayList<>();
			people.addAll(rawData.getInvolvedContacts());

			List<List<String>> topicsAndPeople = nlp.splitNamesAndTopics(topics);
			topics = topicsAndPeople.get(0);
			people.addAll(topicsAndPeople.get(1));
			processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
			processedData.setInvolvedContacts(people.toArray(new String[0]));
		}
		else
			System.out.println("ERROR: No NaturalLanguageProcessor specified.");

		return processedData;
	}

	/**
	* Pushes the ProcessedData to the ProcessedDataQueue.
	* @param processedData The data that has been processed that needs to be sent to the queue for persistence.
	*/
	public void pushToQueue(ProcessedData processedData, boolean priority) {
		// System.out.println("Sending processedData for user: " + processedData.getUserId() + " priority: " + priority);
		if (priority)
        	rabbitTemplate.convertAndSend("priority-" + processedDataDatabaseQueueName, processedData);
		else
        	rabbitTemplate.convertAndSend(processedDataDatabaseQueueName, processedData);
	}

	/**
	* This will stop the thread from checking for new data in the RawDataQueue.
	*/
	public void stop() {
		stop = true;
	}

	public CountDownLatch getLatch() {
		return latch;
	}
}
