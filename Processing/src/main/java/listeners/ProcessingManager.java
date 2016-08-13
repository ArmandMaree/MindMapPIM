package listeners;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.concurrent.CountDownLatch;

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
		private final SimpleMessageListenerContainer processingManagerContainer;

		public ShutDownHookThread(SimpleMessageListenerContainer processingManagerContainer) {
			this.processingManagerContainer = processingManagerContainer;
		}

		@Override
		public void run() {
			processingManagerContainer.setMaxConcurrentConsumers(0);

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

	private int NUM_PROCESSORS = 10;
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private CountDownLatch latch = new CountDownLatch(1);
	private String processedDataDatabaseQueueName = "processed-data.database.rabbit";
	private final static String rawDataQueueName = "raw-data.processing.rabbit";

	@Autowired
	private NaturalLanguageProcessor nlp;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private final SimpleMessageListenerContainer processingManagerContainer = null;

	private LinkedBlockingQueue<RawData> rawDataQueue = new LinkedBlockingQueue<>();
	private List<Processor> processors = new ArrayList<>();
	private List<Thread> processorsThreads = new ArrayList<>();

	/**
	* Default constructor.
	*/
	public ProcessingManager() {
		for (int i = 0; i < NUM_PROCESSORS; i++) {
			Processor processor = new Processor(rawDataQueue, rabbitTemplate, nlp);
			processors.add(processor);
			Thread thread = new Thread(processor);
			processorsThreads.add(thread);
			thread.start();
		}

		Runtime.getRuntime().addShutdownHook(new ShutDownHookThread(processingManagerContainer));
	}

	/**
	* Receives a rawData object and sends it to processors.
	* @param rawData The rawData object that needs processing.
	*/
	public void receiveRawData(RawData rawData) {
		try {
			rawDataQueue.put(rawData);
		}
		catch (InterruptedException ignore) {}
	}
}
