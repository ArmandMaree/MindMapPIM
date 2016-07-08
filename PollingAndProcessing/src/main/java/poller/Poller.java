package poller;

import data.*;

public interface Poller extends Runnable {
	public RawData poll();
	public void addToQueue(RawData data);
}