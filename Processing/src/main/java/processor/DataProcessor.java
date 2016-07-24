package processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import data.*;

/**
* Receives raw data and processes it with a NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-14
*/
public class DataProcessor {
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private NaturalLanguageProcessor nlp;
	private CountDownLatch latch = new CountDownLatch(1);
	private RabbitTemplate rabbitTemplate;

	/**
	* Constructor that initializes some fields.
	* @param nlp An instance of a NaturalLanguageProcessor. If null then the RawDataQueue will just be dequeued as new data is inserted.
	* @param rabbitTemplate A template to a RabbitMQ instance to send messages to.
	*/
	public DataProcessor(NaturalLanguageProcessor nlp, RabbitTemplate rabbitTemplate) {
		this.nlp = nlp;
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	* Receives a rawData object and processes it.
	* @param rawData The rawData object that needs processing.
	*/
	public void receiveRawData(RawData rawData) {
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

			topics = nlp.purge(topics);

			processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
		}

		return processedData;
	}

	/**
	* Pushes the ProcessedData to the ProcessedDataQueue.
	* @param processedData The data that has been processed that needs to be sent to the queue for persistence.
	*/
	public void pushToQueue(ProcessedData processedData) {
        rabbitTemplate.convertAndSend("processed-data", processedData);
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
