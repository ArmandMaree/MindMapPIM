package poller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.PagedList;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.AmqpException;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;

/**
* Uses the Facebook API to retrieve new activity and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FacebookPoller implements Runnable {
	private RabbitTemplate rabbitTemplate;
	private final static String rawDataQueue = "raw-data.processing.rabbit";
	private long expireTime;
	private Facebook service;
	private boolean stop = false;
	private boolean firstPageDone = false;
	private boolean oldDone = false;
	private int offset = 0;
	private int LIMIT = 25;
	private String userId;

	public FacebookPoller(RabbitTemplate rabbitTemplate, String authCode, long expireTime) {
		this.rabbitTemplate = rabbitTemplate;
		service = getService(authCode);
		this.expireTime = expireTime;
		userId = service.userOperations().getUserProfile().getId();
	}

	public Facebook getService(String authCode) {
		String REDIRECT_URI = "https://bubbles.iminsys.com/";
		FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider("1051696778242173", "22a06683d76460f1522396944e7e0506", "datamine");
		return facebookServiceProvider.getApi(authCode);
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				if (expireTime <= System.currentTimeMillis()) {
					System.out.println("STOPPED");
					return;
				}

				poll();

				if (!oldDone)
					oldDone = true;

				Thread.sleep(60 * 1000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void poll() {
		System.out.println("Polling");
		PagedList<Post> feed = service.feedOperations().getFeed(new PagingParameters(null, null, null, null));
		System.out.println(feed.size());

		while (feed != null && offset < 200) {
			for (Post post : feed) {
				RawData rawData = getRawData(post);
				sendToQueue(rawData);
			}

			if (feed.getNextPage() == null)
				feed = null;
			else
				feed = service.feedOperations().getFeed(feed.getNextPage());

			if (!firstPageDone)
				firstPageDone = true;
		}

		offset += LIMIT;
	}

	public RawData getRawData(Post post) {
		List<String> text = new ArrayList<>();
		text.add(post.getMessage());

		RawData rawData = new RawData("facebook", userId, null, post.getId(), text.toArray(new String[0]), post.getCreatedTime().getTime());
		return rawData;
	}

	public void sendToQueue(RawData rawData) {
		try {
				System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userId + " firstPageDone: " + firstPageDone + " oldDone: " + oldDone);

				if (!firstPageDone || oldDone)
					rabbitTemplate.convertAndSend("priority-" + rawDataQueue, rawData);
				else
					rabbitTemplate.convertAndSend(rawDataQueue, rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
		}
	}
}
