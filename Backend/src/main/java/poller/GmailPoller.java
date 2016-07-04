package poller;

import java.util.concurrent.ConcurrentLinkedQueue;

import data.*;

public class GmailPoller implements Poller {
	private ConcurrentLinkedQueue queue;
	private String userId;
	
	public GmailPoller(ConcurrentLinkedQueue queue, String userId) {
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
		queue.add(data);
	}
}
