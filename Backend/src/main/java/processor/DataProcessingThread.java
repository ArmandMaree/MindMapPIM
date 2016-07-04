package processor;

import java.util.concurrent.ConcurrentLinkedQueue;

import data.*;

public class DataProcessingThread implements Runnable {
	private ConcurrentLinkedQueue<RawData> queue;
	private boolean stop = false;

	public DataProcessingThread(ConcurrentLinkedQueue<RawData> queue) {
		this.queue = queue;
	}

	public void run() {
		RawData rd = new RawData();
		rd.pimSource = "pim";
		rd.userId = "uid";
		String[] contacts = {"Armand", "Maree"};
		rd.involvedContacts = contacts;
		String[] dataArr = {"hello", "dude"};
		rd.data = dataArr;
		rd.pimItemId = "itemid";

		String[] topics = {"t1", "t2"};

		ProcessedData pd = new ProcessedData(rd, topics);

		System.out.println(rd);
		System.out.println(pd);

		while (!stop) {
			RawData rawData = null;

			while ((rawData = queue.poll()) == null);


		}
	}

	public void pushToQueue() {

	}
}