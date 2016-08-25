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
* Receives {@link data.RawData} and adds it to a queue where worker threads will dequeue and process it.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ProcessingManager {
	/**
	* Used to save topics in the queue when the server has to shutdown so that they can be resumed when the server starts up again.
	*/
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

	/**
	* Name of the queue that {@link data.RawData} gets sent to. Used in this class to requeue {@link data.RawData} objects that has to be saved due to a server shutdown.
	*/
	private final static String rawDataQueueName = "raw-data.processing.rabbit";

	/**
	* Number of worker threads that process topics with the {@link nlp.NaturalLanguageProcessor}.
	*/
	private int NUM_PROCESSORS = 5;

	/**
	* The {@link nlp.NaturalLanguageProcessor} that will be used to analyse the {@link data.RawData}.
	*/
	private NaturalLanguageProcessor nlp;

	/**
	* A low priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	*/
	private LinkedBlockingQueue<RawData> rawDataQueue = new LinkedBlockingQueue<>();

	/**
	* A high priority {@link java.util.concurrent.LinkedBlockingQueue} that temporarily stores {@link data.RawData} objects while they wait for processor threads to dequeue them.
	*/
	private LinkedBlockingQueue<RawData> priorityRawDataQueue = new LinkedBlockingQueue<>();

	/**
	* A {@link java.util.List} containing all the {@link nlp.Processor} objects that process the {@link data.RawData}.
	*/
	private List<Processor> processors = new ArrayList<>();

	/**
	* A {@link java.util.List} containing all the threads that are running the {@link nlp.Processor} objects.
	*/
	private List<Thread> processorsThreads = new ArrayList<>();

	private RabbitTemplate rabbitTemplate;

	/**
	* Constructor used by the {@link main.Application} to pass all the required beans.
	* @param naturalLanguageProcessor The {@link nlp.NaturalLanguageProcessor} that should be used.
	* @param rabbitTemplate RabbitMQ template used to send messages with.
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

	/**
	* Sets a shutdown hook that will store the messages in the two {@link data.RawData} queues for when the server has to shut down.
	* @param processingManagerContainer The AMQP container that contains the adapters to this class.
	*/
	public void createShutDownHook(SimpleMessageListenerContainer processingManagerContainer) {
		// Runtime.getRuntime().addShutdownHook(new ShutDownHookThread(processingManagerContainer));
	}

	/**
	* Receives a rawData object and sends it to a low priority queue for processing.
	*
	* <p>
	*	If the internal queue has 50 items or more, the {@link data.RawData} object will be sent back to the RawData AMQP queue to preserve to the memory this application has.
	* </p>
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

	/**
	* Receives a rawData object and sends it to a high priority queue for processing.
	*
	* <p>
	*	If the internal queue has 50 items or more, the {@link data.RawData} object will be sent back to the RawData AMQP queue to preserve to the memory this application has.
	* </p>
	* @param rawData The rawData object that needs processing.
	*/
	public synchronized void receivePriorityRawData(RawData rawData) {
		System.out.println("Received priorityRawData for user: " + rawData.getUserId());
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
