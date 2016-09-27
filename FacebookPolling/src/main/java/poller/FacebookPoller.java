package poller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.RevokedAuthorizationException;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;
import com.unclutter.poller.*;

/**
* Uses the Facebook API to retrieve new activity and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FacebookPoller implements Runnable, Poller {
	private MessageBroker messageBroker;
	private final static String rawDataQueue = "raw-data.processing.rabbit";
	private long expireTime;
	private Facebook service;
	private boolean stop = false;
	private boolean firstPageDone = false;
	private boolean oldDone = false;
	private int offset = 0;
	private int LIMIT = 25;
	private String userId;
	private String lastId = "";

	public FacebookPoller(MessageBroker messageBroker, String authCode, long expireTime, String userId) {
		this.messageBroker = messageBroker;
		service = getService(authCode);
		this.expireTime = expireTime + System.currentTimeMillis() / 1000;
		this.userId = userId;
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
				if (expireTime <= System.currentTimeMillis()  / 1000) {
					System.out.println("STOPPED");
					return;
				}

				poll();

				if (!oldDone)
					oldDone = true;

				Thread.sleep(60 * 1000);
			}
			catch (org.springframework.social.RevokedAuthorizationException rae) {
				System.out.println("Auth revoked: ");
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
		PagedList<Post> feed = service.feedOperations().getFeed(new PagingParameters(null, null, null, null));
		System.out.println(feed.size());
		boolean set = false;
		boolean broke = false;;

		while (feed != null && offset < 200) {
			for (Post post : feed) {
				if (post.getId().equals(lastId)) {
					broke = true;
					break;
				}

				if (!set) {
					lastId = post.getId(); 
					set = true;
				}

				RawData rawData = getRawData(post);

				if (rawData == null)
					continue;
				
				addToQueue(rawData);
			}

			if (feed.getNextPage() == null || broke)
				feed = null;
			else
				feed = service.feedOperations().getFeed(feed.getNextPage());

			if (!firstPageDone)
				firstPageDone = true;
		}

		offset += LIMIT;
	}

	public RawData getRawData(Post post) {
		if (post.getMessage() == null || post.getMessage().length() == 0)
			return null;
		else
			System.out.println("Post from: " + post.getFrom().getId());
		
		List<String> text = new ArrayList<>();
		List<String> contacts = new ArrayList<>();
		text.add(post.getMessage());

		String postId;

		if (post.getFrom().getId().equals(userId))
			postId = post.getId();
		else {
			contacts.add(post.getFrom().getName());
			postId = post.getId() + ":" + post.getFrom().getId();
		}

		PagedList<Comment> comments = service.commentOperations().getComments(post.getId(), new PagingParameters(null, null, null, null));

		while (comments != null) {
			for (Comment comment : comments) {
				text.add(comment.getMessage());

				if (!comment.getFrom().getId().equals(userId))
					contacts.add(comment.getFrom().getName());
			}

			if (comments.getNextPage() == null)
				comments = null;
			else
				comments = service.commentOperations().getComments(post.getId(), comments.getNextPage());
		}

		RawData rawData = new RawData("facebook", userId, contacts, postId, text.toArray(new String[0]), post.getCreatedTime().getTime());
		// rawData.setInvolvedContacts(contacts);
		return rawData;
	}

	public void addToQueue(RawData rawData) {
		try {
				System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userId + " firstPageDone: " + firstPageDone);

				if (!firstPageDone || oldDone)
					messageBroker.sendPriorityRawData(rawData);
				else
					messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}
}
