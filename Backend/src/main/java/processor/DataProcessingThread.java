package processor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import data.*;

public class DataProcessingThread implements Runnable {
	private LinkedBlockingQueue<RawData> queue;
	private boolean stop = false;
	private final int TIMEOUT = 10;

	public DataProcessingThread(LinkedBlockingQueue<RawData> queue) {
		this.queue = queue;
	}

	public void run() {
		// test start
		RawData rd = new RawData();
		rd.pimSource = "Gmail";
		rd.userId = "fakeUserId";
		String[] contacts = {"koos@gmail.com", "piet@gmail.com"};
		rd.involvedContacts = contacts;
		String[] dataArr = {"Hey Acuben", "Here's that photo I promised you!"};
		rd.data = dataArr;
		rd.pimItemId = "fakeItemId";
		try {
			queue.put(rd);
		}
		catch (InterruptedException ie) {
			ie.printStackTrace();
			System.exit(1);
		}
		// test end

		while (!stop) {
			RawData rawData = null;

			try {
				if ((rawData = queue.poll(TIMEOUT, TimeUnit.SECONDS)) == null) {
					System.out.println("TIMED OUT");
					continue;
				}
			}
			catch (InterruptedException ie) {
				System.out.println("Interrupted");
				continue;
			}

			System.out.println(rawData);
		}
	}

	public void pushToQueue() {

	}
}