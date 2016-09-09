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
* Receives {@link data.RawData} and processes it with a {@link nlp.NaturalLanguageProcessor}.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class Processor implements Runnable {
	/**
	* Indicates whether the processor should stop.
	*/
	private boolean stop = false;

	/**
	* Name of the queue that {@link data.ProcessedData} should be sent to.
	*/
	private String processedDataDatabaseQueueName = "processed-data.database.rabbit";

	/**
	* The {@link nlp.NaturalLanguageProcessor} that should be used.
	*/
	private NaturalLanguageProcessor nlp;

	/**
	* A low priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	*/
	private LinkedBlockingQueue<RawData> rawDataQueue;

	/**
	* A high priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	*/
	private LinkedBlockingQueue<RawData> priorityRawDataQueue;

	private RabbitTemplate rabbitTemplate;

	/**
	* Constructor used by the {@link listeners.ProcessingManager} to initialize some variables.
	* @param rawDataQueue A low priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	* @param priorityRawDataQueue A high priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	* @param rabbitTemplate RabbitMQ template used to send messages with.
	* @param nlp The {@link nlp.NaturalLanguageProcessor} that should be used.
	*/
	public Processor(LinkedBlockingQueue<RawData> rawDataQueue, LinkedBlockingQueue<RawData> priorityRawDataQueue, RabbitTemplate rabbitTemplate, NaturalLanguageProcessor nlp) {
		this.rawDataQueue = rawDataQueue;
		this.priorityRawDataQueue = priorityRawDataQueue;
		this.rabbitTemplate = rabbitTemplate;
		this.nlp = nlp;
	}

	/**
	* Polls the {@link java.util.concurrent.LinkedBlockingQueue}s for new {@link data.RawData} to process.
	* <p>
	*	The priority queue will always first be emptied before the low priority queue will be processed. If the priority queue is empty then the low priority queue will be polled for 10 seconds at a time before checking whether it should stop.
	* </p>
	*/
	public void run() {
		while (true) {
			try {
				boolean priority = false;
				RawData rawData = priorityRawDataQueue.poll();

				if (rawData == null)
					rawData = rawDataQueue.poll(10, TimeUnit.SECONDS);
				else
					priority = true;

				if (rawData == null) {
					Thread.sleep(10);
					continue;
				}

				System.out.println("Processing: some rawData in thread " + Thread.currentThread().getName());

				ProcessedData processedData = process(rawData);

				if (processedData == null)
					continue;

				pushToQueue(processedData, priority);
			}
			catch (Throwable ignore) {
				System.out.println(Thread.currentThread().getName() + " Caught in run");
				ignore.printStackTrace();
			}
		}

		// System.out.println(Thread.currentThread().getName() + " i was told to stop: " + stop);
	}

	/**
	* Processes rawData with the {@link nlp.NaturalLanguageProcessor}.
	* @param rawData The object that has to be processed.
	* @return The processed data.
	*/
	public ProcessedData process(RawData rawData) {
		List<String> topics = new ArrayList<>();
		ProcessedData processedData = null;

		if (nlp != null) {
			for (String part : rawData.getData()) {
				List<String> topicsIdentified = nlp.getTopics(part);

				for (String topic : topicsIdentified)
					topics.add(topic);
			}

			if (topics.size() == 0)
				return null;

			topics = nlp.purge(topics);

			List<String> people = new ArrayList<>();

			if (rawData.getInvolvedContacts() != null) {
				people.addAll(rawData.getInvolvedContacts());
				people = nlp.purge(people);
			}
			
			processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
			processedData.setInvolvedContacts(people.toArray(new String[0]));
		}
		else
			System.out.println("ERROR: No NaturalLanguageProcessor specified.");

		return processedData;
	}

	/**
	* Pushes the ProcessedData to the ProcessedDataQueue.
	* @param processedData The data that has been processed that needs to be sent to the processed data queue for persistence.
	* @param priority Indicates whether the processedData should be placed on the priority queue or not.
	*/
	public void pushToQueue(ProcessedData processedData, boolean priority) {
		System.out.println("Sending processedData for user: " + processedData.getUserId() + " priority: " + priority);
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
}
