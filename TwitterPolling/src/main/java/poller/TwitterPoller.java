package poller;

import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageNotSentException;
import com.unclutter.poller.RawData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import repositories.TwitterRepository;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
* Uses the Twitter API to retrieve new activity and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class TwitterPoller implements Runnable {
	private static final String CONSUMER_KEY = "6PVgLYY8uIa3zBAwqss3ogPkA";
	private static final String CONSUMER_SECRET = "v8PiSDzChX9qo4nirNsI26oGbSvIvrKKx9iM3fNHeAWbSYSXSS";
	private static final String ACCESS_TOKEN = "782990014204502018-WNCVqfNC4onMAjdA0iJbAQX9vqZCouo";
	private static final String ACCESS_TOKEN_SECRET = "HZS5qZt6znwhI56ZQPCZypuosKER2COGhwuknZAvysLdy";
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private TwitterRepository twitterRepository;
	private MessageBroker messageBroker;
	private Twitter service = null;
	private boolean stop = false;
	private int tweetCount = 0;
	private int PRIORITY_LIMIT = 25;
	private int MAX_OLD_TWEETS = 50;
	private int DELAY_BETWEEN_POLLS = 60; // 60 seconds delay between polls
	private String userId;
	private long lastId = -1;
	private TwitterPollingUser pollingUser;

	/**
	* Constructor.
	* @param twitterRepository The repository that will be used to persist information of each user that is being polled for. {@link poller.TwitterPollingUser}
	* @param messageBroker Used to send messages with.
	* @param userId The Twitter ID of the user for which this poller is polling for.
	*/
	public TwitterPoller(TwitterRepository twitterRepository, MessageBroker messageBroker, String userId) throws AlreadyPollingForUserException {
		this.twitterRepository = twitterRepository;
		this.messageBroker = messageBroker;
		this.userId = userId;

		pollingUser = twitterRepository.findByUserId(userId);

		if (pollingUser == null) {
			pollingUser = new TwitterPollingUser(userId);
			pollingUser.checkAndStart();
			twitterRepository.save(pollingUser);
			pollingUser = twitterRepository.findByUserId(userId);
			this.service = getService();
		}
		else {
			pollingUser = twitterRepository.findByUserId(pollingUser.getUserId());

			if (pollingUser.checkAndStart()) {
				throw new AlreadyPollingForUserException("There is already a poller running for user " + pollingUser.getUserId() + ".");
			}
			else
				twitterRepository.save(pollingUser);
		}
	}

	/**
	* Creates am authenticated FacebookTwitter service.
	* @return An authenticated Twitter service.
	*/
	public Twitter getService() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(CONSUMER_KEY);
		cb.setOAuthConsumerSecret(CONSUMER_SECRET);
		cb.setOAuthAccessToken(ACCESS_TOKEN);
		cb.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		return new TwitterFactory(cb.build()).getInstance();
	}

	/**
	* Runs the poller on a loop.
	* <p>
	*	A {@link java.util.concurrent.ScheduledExecutorService} will be used to schedule the poller to run with a {@link DELAY_BETWEEN_POLLS} delay.
	* </p>
	*/
	@Override
	public void run() {
		try {
			pollingUser = twitterRepository.findByUserId(pollingUser.getUserId());

			if (service == null)
				System.out.println("No authorized service for user " + userId);
			if (!pollingUser.getCurrentlyPolling())
				System.out.println("Stopping polling for " + userId + " (probably due to stop request).");
			else {
				System.out.println("START");
				poll();

				final ScheduledFuture<?> pollerHandle = scheduler.schedule(this, DELAY_BETWEEN_POLLS, TimeUnit.SECONDS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	* Gets a list of posts and checks to see if the have been processed. If they have not yet been, then it extracts the raw text and creates a RawData object that is pushed to the RawDataQueue.
	*/
	public void poll() throws TwitterException {
		System.out.println("Polling for " + userId);
		ResponseList<Status> timeline = getMoreTweets();
		System.out.println("GOT " + timeline.size() + " tweets.");

		outerloop:
		while (timeline.size() > 0) {
			if (pollingUser.getStartOfBlockTweetId() == 1) {
				pollingUser.setStartOfBlockTweetId(timeline.get(0).getId());
				twitterRepository.save(pollingUser);
			}

			for (Status tweet: timeline) {
				pollingUser = twitterRepository.findByUserId(userId);

				if (!pollingUser.getCurrentlyPolling())
					break outerloop;

				if(pollingUser.getNumberOfTweets() >= MAX_OLD_TWEETS && MAX_OLD_TWEETS != -1) {
					System.out.println("Reached maximum number of tweets for user " + userId + ". Now only checking for new tweets.");
					break outerloop;
				}
				
				RawData rawData = getRawData(tweet);

				if (rawData != null)
					addToQueue(rawData);

				pollingUser.setCurrentTweetId(tweet.getId());
				pollingUser.incrementNumberOfTweets();
				twitterRepository.save(pollingUser);
			}
			
			timeline = getMoreTweets();
		}

		if (pollingUser.getStartOfBlockTweetId() != 1) {
			pollingUser.setEndOfBlockTweetId(pollingUser.getStartOfBlockTweetId());
			pollingUser.setStartOfBlockTweetId(1);
			pollingUser.setCurrentTweetId(1);
			twitterRepository.save(pollingUser);
		}
	}

	/**
	* Gets a list of tweets that has to be processed.
	* <p>
	*	If the poller has previously crashed this method will be able to get only the tweets that should be processed next.
	* </p>
	* @return A list of tweets to be processed. The size will be 0 if there are no new tweets.
	*/
	public ResponseList<Status> getMoreTweets() throws TwitterException {
		ResponseList<Status> response = null;

		// have not started processing yet
		if (pollingUser.getStartOfBlockTweetId() == 1 && pollingUser.getEndOfBlockTweetId() == 1) {
			response = service.getUserTimeline(userId, new Paging(1,50));
		}
		//started processing old emails, but have not finished
		else if (pollingUser.getStartOfBlockTweetId() != 1 && pollingUser.getEndOfBlockTweetId() == 1) {
			long id;

			if (pollingUser.getCurrentTweetId() == 1)// the email block was set but the actual processing hasnt started yet
				id = pollingUser.getStartOfBlockTweetId();
			else
				id = pollingUser.getCurrentTweetId();
			
			response = service.getUserTimeline(userId, new Paging(1, 50, 1, id));

			if (response.size() > 0)
				if (response.get(0).getId() == id)
					response.remove(0);
		}
		// processing a middle block
		else if (pollingUser.getStartOfBlockTweetId() != 1 && pollingUser.getEndOfBlockTweetId() != 1) {
			long id;

			if (pollingUser.getCurrentTweetId() == 1)// the email block was set but the actual processing hasnt started yet
				id = pollingUser.getStartOfBlockTweetId();
			else
				id = pollingUser.getCurrentTweetId();

			response = service.getUserTimeline(userId, new Paging(1, 50, pollingUser.getEndOfBlockTweetId(), id));
			
			if (response.size() > 0)
				if (response.get(0).getId() == id)
					response.remove(0);

			if (response.size() > 0)
				if (response.get(response.size() - 1).getId() == pollingUser.getEndOfBlockTweetId())
					response.remove(response.size() - 1);
		}
		// finished processing old emails and need to process new emails
		else if (pollingUser.getStartOfBlockTweetId() == 1 && pollingUser.getEndOfBlockTweetId() != 1) {
			response = service.getUserTimeline(userId, new Paging(1, 50, pollingUser.getEndOfBlockTweetId()));
			
			if (response.size() > 0)
				if (response.get(response.size() - 1).getId() == pollingUser.getEndOfBlockTweetId())
					response.remove(response.size() - 1);
		}

		return response;
	}

	/**
	* Extract the text from a tweet and parse it as an RawData object.
	* @param tweet The tweet that has to be processed.
	* @return Object that contains the details of the tweet or null if no data is found.
	*/
	public RawData getRawData(Status tweet) {
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

	/**
	* Takes a RawData object and add it to a RawDataQueue.
	* <p>
	*	If the {@link poller.TwitterPollingUser#numberOfPosts} is less than {@link MAX_PRIORITY_TWEETS} then the object will be sent on the priority queue.
	* </p>
	* @param rawData The rawData object that should be added to the queue.
	*/
	public void addToQueue(RawData rawData) {
		try {
			System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userId + " tweetCount: " + (pollingUser.getNumberOfTweets() + 1));

			if (tweetCount < PRIORITY_LIMIT)
				messageBroker.sendPriorityRawData(rawData);
			else
				messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}
}
