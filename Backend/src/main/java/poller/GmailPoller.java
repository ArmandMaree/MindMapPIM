package poller;

import java.util.concurrent.LinkedBlockingQueue;

import data.*;

public class GmailPoller implements Poller {
	private LinkedBlockingQueue queue;
	private String userId;
	
	public GmailPoller(LinkedBlockingQueue queue, String userId) {
		this.queue = queue;
		this.userId = userId;
	}

	public void run() {
		System.out.println("\n\nHello\n\n");
	}

	public RawData poll() {
		return null;
	}

	public void addToQueue(RawData data) {
		queue.put(data);
	}
}
