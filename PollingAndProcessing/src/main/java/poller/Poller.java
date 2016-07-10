package poller;

import data.*;

public interface Poller extends Runnable {
	public void poll();
	public void addToQueue(RawData data);
}
