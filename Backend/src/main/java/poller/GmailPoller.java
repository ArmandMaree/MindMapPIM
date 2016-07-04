package poller;

import java.util.concurrent.ConcurrentLinkedQueue;

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
}