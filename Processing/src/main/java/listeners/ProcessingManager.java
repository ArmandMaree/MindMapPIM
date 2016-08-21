package listeners;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.io.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.*;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import data.*;
import nlp.*;

/**
* Receives raw data and processes it with a NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class ProcessingManager {
	private class ShutDownHookThread extends Thread {
		private SimpleMessageListenerContainer processingManagerContainer;

		public ShutDownHookThread(SimpleMessageListenerContainer processingManagerContainer) {
			this.processingManagerContainer = processingManagerContainer;
		}

		@Override
		public void run() {
			if (processingManagerContainer != null) {
				processingManagerContainer.stop();
				System.out.println("Stopped consumers.");
			}
			else
				System.out.println("Container null.");

			System.out.println("Size of rawDataQueue: " + rawDataQueue.size());

			for (Processor processor : processors)
				processor.stop();

			for (RawData rawData : rawDataQueue)
				rabbitTemplate.convertAndSend(rawDataQueueName, rawData);

			for (Thread thread : processorsThreads)
				try {
					thread.join();
				}
				catch (InterruptedException ignore) {}
		}
	}

	private int NUM_PROCESSORS = 5;
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private CountDownLatch latch = new CountDownLatch(1);
	private String processedDataDatabaseQueueName = "processed-data.database.rabbit";
	private final static String rawDataQueueName = "raw-data.processing.rabbit";
	private NaturalLanguageProcessor nlp;
	private RabbitTemplate rabbitTemplate;
	private LinkedBlockingQueue<RawData> rawDataQueue = new LinkedBlockingQueue<>();
	private LinkedBlockingQueue<RawData> priorityRawDataQueue = new LinkedBlockingQueue<>();
	private List<Processor> processors = new ArrayList<>();
	private List<Thread> processorsThreads = new ArrayList<>();

	/**
	* Default constructor.
	*/
	public ProcessingManager(NaturalLanguageProcessor naturalLanguageProcessor, RabbitTemplate rabbitTemplate) {
		nlp = naturalLanguageProcessor;
		this.rabbitTemplate = rabbitTemplate;

		for (int i = 0; i < NUM_PROCESSORS; i++) {
			Processor processor = new Processor(rawDataQueue, priorityRawDataQueue, rabbitTemplate, nlp);
			processors.add(processor);
			Thread thread = new Thread(processor);
			processorsThreads.add(thread);
			thread.start();
		}
	}

	public void createShutDownHook(SimpleMessageListenerContainer processingManagerContainer) {
		// Runtime.getRuntime().addShutdownHook(new ShutDownHookThread(processingManagerContainer));
	}

	/**
	* Receives a rawData object and sends it to processors.
	* @param rawData The rawData object that needs processing.
	*/
	public synchronized void receiveRawData(RawData rawData) {
		try {
			if (rawDataQueue.size() >= 50) {
				rabbitTemplate.convertAndSend("raw-data.processing.rabbit");
				return;
			}

			rawDataQueue.put(rawData);
		}
		catch (InterruptedException ignore) {}
	}

	public synchronized void receivePriorityRawData(RawData rawData) throws FileNotFoundException {
		try {
			if (priorityRawDataQueue.size() >= 50) {
				rabbitTemplate.convertAndSend("priority-raw-data.processing.rabbit");
				return;
			}

			priorityRawDataQueue.put(rawData);
		}
		catch (InterruptedException ignore) {}
	}
}
