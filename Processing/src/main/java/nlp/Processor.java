package nlp;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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

	/**
	* Default constructor.
	*/
	public Processor(LinkedBlockingQueue<RawData> rawDataQueue, RabbitTemplate rabbitTemplate, NaturalLanguageProcessor nlp) {
		this.rawDataQueue = rawDataQueue;
		this.rabbitTemplate = rabbitTemplate;
		this.nlp = nlp;
	}

	public void run() {
		while (!stop) {
			try {
				RawData rawData = rawDataQueue.take();
				ProcessedData processedData = process(rawData);
				pushToQueue(processedData);
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
		ArrayList<String> topics = new ArrayList<>();
		ProcessedData processedData = null;

		if (nlp != null) {
			for (String part : rawData.getData()) {
				ArrayList<String> topicsIdentified = nlp.getTopics(part);

				for (String topic : topicsIdentified) {
					if (!topics.contains(topic))
						topics.add(topic);
				}
			}

			processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
		}
		else
			System.out.println("ERROR: No NaturalLanguageProcessor specified.");

		return processedData;
	}

	/**
	* Pushes the ProcessedData to the ProcessedDataQueue.
	* @param processedData The data that has been processed that needs to be sent to the queue for persistence.
	*/
	public void pushToQueue(ProcessedData processedData) {
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
