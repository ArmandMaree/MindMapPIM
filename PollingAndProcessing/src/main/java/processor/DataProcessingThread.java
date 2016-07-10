package processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import data.*;

public class DataProcessingThread implements Runnable {
	private RawDataQueue rawQueue;
	private ProcessedDataQueue processedQueue;
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private NaturalLanguageProcessor nlp;

	public DataProcessingThread(RawDataQueue rawQueue, ProcessedDataQueue processedQueue, NaturalLanguageProcessor nlp) {
		this.rawQueue = rawQueue;
		this.processedQueue = processedQueue;
		this.nlp = nlp;
	}

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

			if (nlp != null)
				for (String part : rawData.data)
					topics.addAll(nlp.getTopics(part));

			ProcessedData processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
			// System.out.println("ProcessorDone->" + processedData);
			// pushToQueue(processedData);
		}
	}

	public void pushToQueue(ProcessedData processedData) {
		try {
			processedQueue.put(processedData);
		}
		catch (InterruptedException iee) {
			iee.printStackTrace();
		}
	}

	public void stop() {
		stop = true;
	}
}
