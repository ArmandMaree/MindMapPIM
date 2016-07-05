package processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import data.*;

public class DataProcessingThread implements Runnable {
	private LinkedBlockingQueue<RawData> queue;
	private boolean stop = false;
	private final int TIMEOUT = 10;
	private NaturalLanguageProcessor nlp;

	public DataProcessingThread(LinkedBlockingQueue<RawData> queue, NaturalLanguageProcessor nlp) {
		this.queue = queue;
		this.nlp = nlp;
	}

	public void run() {
		// // test start
		RawData rd = new RawData();
		rd.pimSource = "Gmail";
		rd.userId = "fakeUserId";
		String[] contacts = {"koos@gmail.com", "piet@gmail.com"};
		rd.involvedContacts = contacts;
		String[] dataArr = {"Hey Acuben", "Here's that photo I promised you!", "Welcome to New York City!"};
		rd.data = dataArr;
		rd.pimItemId = "fakeItemId";
		try {
			queue.put(rd);
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
			System.exit(1);
		}
		// // test end

		while (!stop) {
			RawData rawData = null;

			try {
				if ((rawData = queue.poll(TIMEOUT, TimeUnit.SECONDS)) == null)
					continue;
			}
			catch (InterruptedException ie) {
				continue;
			}

			System.out.println("ProcessorDequeued->" + rawData);
			ArrayList<String> topics = new ArrayList<>();

			for (String part : rawData.data)
				topics.addAll(nlp.getTopics(part));

			ProcessedData processedData = new ProcessedData(rawData, topics.toArray(new String[0]));
			System.out.println("ProcessorDone->" + processedData);
		}
	}

	public void pushToQueue() {

	}

	public void stop() {
		stop = true;
	}
}
