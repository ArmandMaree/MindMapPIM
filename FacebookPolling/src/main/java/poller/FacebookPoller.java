package poller;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.RevokedAuthorizationException;

import org.springframework.beans.factory.annotation.Autowired;

import data.*;
import repositories.*;
import com.unclutter.poller.*;

/**
* Uses the Facebook API to retrieve new activity and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class FacebookPoller implements Runnable, Poller {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private MessageBroker messageBroker;
	private FacebookRepository facebookRepository;
	private long expireTime;
	private Facebook service;
	private boolean stop = false;
	private int MAX_PRIORITY_POSTS = 50;
	private int MAX_POSTS = 200;
	private int DELAY_BETWEEN_POLLS = 60; // 60 seconds delay between polls
	private FacebookPollingUser pollingUser;
	private String userId;

	public FacebookPoller(FacebookRepository facebookRepository, MessageBroker messageBroker, String authCode, long expireTime, String userId) throws AlreadyPollingForUserException {
		this.facebookRepository = facebookRepository;
		this.messageBroker = messageBroker;
		this.service = getService(authCode);
		this.expireTime = expireTime + System.currentTimeMillis() / 1000;
		this.userId = userId;

		pollingUser = facebookRepository.findByUserId(userId);

		if (pollingUser == null) {
			pollingUser = new FacebookPollingUser(userId, authCode, expireTime);
			pollingUser.checkAndStart();
			facebookRepository.save(pollingUser);
			pollingUser = facebookRepository.findByUserId(userId);
		}
		else {
			pollingUser = facebookRepository.findByUserId(pollingUser.getUserId());

			if (pollingUser.checkAndStart()) {
				throw new AlreadyPollingForUserException("There is already a poller running for user " + pollingUser.getUserId() + ".");
			}
			else
				facebookRepository.save(pollingUser);
		}
	}

	public Facebook getService(String authCode) {
		String REDIRECT_URI = "https://bubbles.iminsys.com/";
		FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider("1051696778242173", "22a06683d76460f1522396944e7e0506", "datamine");
		return facebookServiceProvider.getApi(authCode);
	}

	@Override
	public void run() {
		try {
			pollingUser = facebookRepository.findByUserId(pollingUser.getUserId());

			if (!pollingUser.getCurrentlyPolling())
				System.out.println("Stopping polling for " + userId + " (probably due to stop request).");
			else if(pollingUser.getNumberOfPosts() > MAX_POSTS)
				System.out.println("Reached maximum number of posts for user " + userId);
			else {
				poll();

				final ScheduledFuture<?> pollerHandle = scheduler.schedule(this, DELAY_BETWEEN_POLLS, TimeUnit.SECONDS);
			}
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

	public void poll() {
		System.out.println("Polling");
		// /service.feedOperations().getFeed(new PagingParameters(null, null, null, null))
		PagedList<Post> feed = getMorePosts();

		outerloop:
		while (feed.size() > 0) {
			if (pollingUser.getStartOfBlockPostId() == null) {
				pollingUser.setStartOfBlockPostId(feed.get(0).getId());
				facebookRepository.save(pollingUser);
			}

			for (Post post : feed) {
				pollingUser = facebookRepository.findByUserId(userId);

				if (pollingUser.getCurrentlyPolling() || pollingUser.getNumberOfPosts() > MAX_POSTS)
					break outerloop;

				RawData rawData = getRawData(post);

				if (rawData == null)
					continue;
				
				addToQueue(rawData);

				pollingUser.setCurrentPostId(post.getId());
				pollingUser.incrementNumberOfPosts();
				facebookRepository.save(pollingUser);
			}

			feed = getMorePosts();
		}

		if (pollingUser.getStartOfBlockPostId() != null) {
			pollingUser.setEndOfBlockPostId(pollingUser.getStartOfBlockPostId());
			pollingUser.setStartOfBlockPostId(null);
			pollingUser.setCurrentPostId(null);
			facebookRepository.save(pollingUser);
		}
	}

	public PagedList<Post> getMorePosts() {
		PagedList<Post> response = null;

		// have not started processing yet
		if (pollingUser.getStartOfBlockPostId() == null && pollingUser.getEndOfBlockPostId() == null) {
			response = service.feedOperations().getFeed(userId, new PagingParameters(50, null, null, null));
		}
		//started processing old emails, but have not finished
		else if (pollingUser.getStartOfBlockPostId() != null && pollingUser.getEndOfBlockPostId() == null) {
			String id;

			if (pollingUser.getCurrentPostId() == null)// the email block was set but the actual processing hasnt started yet
				id = pollingUser.getStartOfBlockPostId();
			else
				id = pollingUser.getCurrentPostId();

			Long timestamp = service.feedOperations().getPost(id).getCreatedTime().getTime();
			
			response = service.feedOperations().getFeed(userId, new PagingParameters(50, null, null, timestamp));

			if (response.size() > 0)
				if (response.get(0).getId() == id)
					response.remove(0);
		}
		// processing a middle block
		else if (pollingUser.getStartOfBlockPostId() != null && pollingUser.getEndOfBlockPostId() != null) {
			String id;

			if (pollingUser.getCurrentPostId() == null)// the email block was set but the actual processing hasnt started yet
				id = pollingUser.getStartOfBlockPostId();
			else
				id = pollingUser.getCurrentPostId();

			Long timestampStart = service.feedOperations().getPost(pollingUser.getEndOfBlockPostId()).getCreatedTime().getTime();
			Long timestampEnd = service.feedOperations().getPost(id).getCreatedTime().getTime();
			
			response = service.feedOperations().getFeed(userId, new PagingParameters(50, null, timestampStart, timestampEnd));

			if (response.size() > 0)
				if (response.get(0).getId() == id)
					response.remove(0);

			if (response.size() > 0)
				if (response.get(response.size() - 1).getId() == pollingUser.getEndOfBlockPostId())
					response.remove(response.size() - 1);
		}
		// finished processing old emails and need to process new emails
		else if (pollingUser.getStartOfBlockPostId() == null && pollingUser.getEndOfBlockPostId() != null) {
			Long timestamp = service.feedOperations().getPost(pollingUser.getEndOfBlockPostId()).getCreatedTime().getTime();
			
			response = service.feedOperations().getFeed(userId, new PagingParameters(50, null, timestamp, null));

			if (response.size() > 0)
				if (response.get(response.size() - 1).getId() == pollingUser.getEndOfBlockPostId())
					response.remove(response.size() - 1);
		}

		return response;
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
			System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userId + " postCount: " + pollingUser.getNumberOfPosts());

			if (pollingUser.getNumberOfPosts() <= MAX_PRIORITY_POSTS)
				messageBroker.sendPriorityRawData(rawData);
			else
				messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}
}
