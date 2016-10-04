package poller;

import com.unclutter.poller.RawData;

/**
* Basic interface that defines a poller.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface Poller extends Runnable {
	/**
	* Gets a list of emails and checks to see if the have been processed. If they have not yet been, then it extracts the raw text and creates a RawData object that is pushed to the RawDataQueue.
	*/
	public void poll();

	/**
	* Takes a RawData object and add it to a RawDataQueue.
	* @param rawData The rawData object that should be added to the queue.
	*/
	public void addToQueue(RawData rawData);
}
