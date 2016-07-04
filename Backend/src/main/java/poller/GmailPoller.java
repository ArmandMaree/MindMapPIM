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
		//Polling to see if a new email has arrived
		return null;
	}

	public void addToQueue(RawData data) {
		try {
			queue.put(data);
		}
		catch (InterruptedException ex) {
			System.out.println("Interrupted while waiting");		
		}
	}
}
