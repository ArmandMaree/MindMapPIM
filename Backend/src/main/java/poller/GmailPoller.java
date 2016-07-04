package poller;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GmailPoller implements Poller {
	public GmailPoller(ConcurrentLinkedQueue queue, String userId) {

	}

	public void run() {
		System.out.println("\n\nHello\n\n");
	}
}