package listeners;

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
public class RawDataListener {
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private CountDownLatch latch = new CountDownLatch(1);
	private String processedDataDatabaseQueueName = "processed-data.database.rabbit";

	@Autowired
	private NaturalLanguageProcessor nlp;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	* Default constructor.
	*/
	public RawDataListener() {

	}

	/**
	* Receives a rawData object and processes it.
	* @param rawData The rawData object that needs processing.
	*/
	public void receiveRawData(RawData rawData) {
		System.out.println("Processor received: " + rawData);
		ProcessedData processedData = process(rawData);
		pushToQueue(processedData);
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
			for (String part : rawData.getData())
				topics.addAll(nlp.getTopics(part));

			topics = nlp.purge(topics); // remove duplicates and excluded words.

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
