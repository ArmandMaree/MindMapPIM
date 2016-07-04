package poller;

import java.util.concurrent.ConcurrentLinkedQueue;

import data.*;

public class GmailPoller implements Poller {
	public GmailPoller(ConcurrentLinkedQueue<RawData> queue, String userId) {

	}

	public void run() {
		System.out.println("\n\nHello\n\n");
	}
}