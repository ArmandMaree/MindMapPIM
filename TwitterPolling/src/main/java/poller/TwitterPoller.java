package poller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.Tweet;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;
import com.unclutter.poller.*;

/**
* Uses the Twitter API to retrieve new activity and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class TwitterPoller implements Runnable, Poller {
	private MessageBroker messageBroker;
	private Twitter service;
	private boolean stop = false;
	private int tweetCount = 0;
	private int LIMIT = 50;
	private String userId;
	private long lastId = -1;

	public TwitterPoller(MessageBroker messageBroker, String authCode, long expireTime, String userId) {
		this.messageBroker = messageBroker;
		service = getService(authCode);
		this.userId = userId;
	}

	public Twitter getService(String authCode) {
		String REDIRECT_URI = "https://bubbles.iminsys.com/";
		TwitterServiceProvider twitterServiceProvider = new TwitterServiceProvider("EjHZpoRbmS61AwkVsRMK1wAHN", "O5LaCzhD4s3G8EBcRqjYoDTJyR6RTQs20Amn7WvRfyos0wlWeA");
		return twitterServiceProvider.getApi(authCode, "O5LaCzhD4s3G8EBcRqjYoDTJyR6RTQs20Amn7WvRfyos0wlWeA");
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				poll();

				if (tweetCount > 200) {
					System.out.println("Stopping due to 200 tweet limit set by Armand.");
					return;
				}

				Thread.sleep(60 * 1000);
			}
			catch (org.springframework.social.RevokedAuthorizationException rae) {
				rae.printStackTrace();
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void poll() {
		System.out.println("Polling");
		List<Tweet> timeline = service.timelineOperations().getHomeTimeline(200);
		long earliestId = -1;

		while (timeline.size() > 0) {
			for (Tweet tweet: timeline) {
				if (tweet.getId() == lastId) {
					lastId = timeline.get(0).getId();
					return;
				}

				RawData rawData = getRawData(tweet);
				tweetCount++;

				if (rawData != null)
					addToQueue(rawData);

				earliestId = tweet.getId();
			}
			
			timeline = service.timelineOperations().getHomeTimeline(200, -1, earliestId);

			if (timeline.get(0).getId() == earliestId)
				timeline.remove(0);
		}

		lastId = timeline.get(0).getId();
	}

	public RawData getRawData(Tweet tweet) {
		if (tweet.getText() == null || tweet.getText().length() == 0)
			return null;
		
		List<String> text = new ArrayList<>();
		List<String> contacts = new ArrayList<>();
		text.add(tweet.getText());

		String tweetId = tweet.getId()  + "";

		RawData rawData = new RawData("twitter", userId, contacts, tweetId, text.toArray(new String[0]), tweet.getCreatedAt().getTime());
		// rawData.setInvolvedContacts(contacts);
		return rawData;
	}

	public void addToQueue(RawData rawData) {
		try {
				System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userId + " tweetCount: " + tweetCount);

				if (tweetCount < LIMIT)
					messageBroker.sendPriorityRawData(rawData);
				else
					messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}
}
