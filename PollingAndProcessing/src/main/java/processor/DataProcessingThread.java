package processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import data.*;

/**
* Thread that pulls information from a RawDataQueue and processes it with a NaturalLanguageProcessor.
*
* @author  Armand Maree
* @since   2016-07-11
*/
public class DataProcessingThread implements Runnable {
	private RawDataQueue rawQueue;
	private ProcessedDataQueue processedQueue;
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private NaturalLanguageProcessor nlp;

	/**
	* Constructor that initializes some fields.
	* @param rawQueue This queue contains the raw text extracted from various pollers that need to get processed.
	* @param processedQueue This queue contains the information in their processed state ready to be sent to the database for persistence.
	* @param nlp An instance of a NaturalLanguageProcessor. If null then the RawDataQueue will just be dequeued as new data is inserted.
	*/
	public DataProcessingThread(RawDataQueue rawQueue, ProcessedDataQueue processedQueue, NaturalLanguageProcessor nlp) {
		this.rawQueue = rawQueue;
		this.processedQueue = processedQueue;
		this.nlp = nlp;
	}

	/**
	* This method will constantly check to see if there is new data in the RawDataQueue and process it accordingly. Afterwards it will place the processed data in the ProcessedDataQueue.
	*/
	public void run() {
		// // test start
		// RawData rd = new RawData();
		// rd.pimSource = "Gmail";
		// rd.userId = "fakeUserId";
		// String[] contacts = {"koos@gmail.com", "piet@gmail.com"};
		// rd.involvedContacts = contacts;
		// String[] dataArr = {"Hey Acuben", "Here's that photo I promised you!", "Stevie Wonder lives in New York City."};
		// rd.data = dataArr;
		// rd.pimItemId = "fakeItemId";
		// try {
		// 	rawQueue.put(rd);
		// }
		// catch (InterruptedException ie) {
		// 	ie.printStackTrace();
		// 	System.exit(1);
		// }
		// // test end

		while (!stop) {
			RawData rawData = null;

			try {
				if ((rawData = rawQueue.poll(TIMEOUT, TimeUnit.SECONDS)) == null)
					continue;
			}
			catch (InterruptedException ie) {
				continue;
			}

			ArrayList<String> topics = new ArrayList<>();

			if (nlp != null) {
				for (String part : rawData.data)
					topics.addAll(nlp.getTopics(part));

				ProcessedData processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
				// System.out.println("ProcessorDone->" + processedData);
				// pushToQueue(processedData);
			}
		}
	}

	/**
	* Pushes the ProcessedData to the ProcessedDataQueue.
	* @param processedData The data that has been processed that needs to be sent to the queue for persistence.
	*/
	public void pushToQueue(ProcessedData processedData) {
		try {
			processedQueue.put(processedData);
		}
		catch (InterruptedException iee) {
			iee.printStackTrace();
		}
	}

	/**
	* This will stop the thread from checking for new data in the RawDataQueue.
	*/
	public void stop() {
		stop = true;
	}
}
