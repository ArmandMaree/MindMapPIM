package poller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ListMessagesResponse;

import com.unclutter.poller.MessageBroker;
import com.unclutter.poller.MessageNotSentException;
import com.unclutter.poller.RawData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import repositories.GmailRepository;

/**
* Uses the Gmail API to retrieve new emails and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class GmailPoller implements Runnable{
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private volatile boolean stop = false;
	private Gmail service = null;
	private GmailRepository gmailRepository;
	private MessageBroker messageBroker;
	private int MAX_EMAILS = -1;
	private int MAX_OLD_EMAILS = 50;
	private int MAX_PRIORITY_EMAILS = 25;
	private boolean oldDone = false;
	private int DELAY_BETWEEN_POLLS = 60; // 60 seconds delay between polls
	private GmailPollingUser pollingUser = null;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	* Constructor that initializes some fields and starts up a Gmail service.
	* @param gmailRepository The repository where all the users and their refresh tokens are stored.
	* @param messageBroker Reference to a messageBroker used to communicate with a RabbitMQ server.
	* @param userAuthCode Authentication code received from the login service.
	* @param userEmail The email address of the user.
	*/
	public GmailPoller(GmailRepository gmailRepository, MessageBroker messageBroker, String userAuthCode, String userEmail) throws IOException, UserNotFoundException, AlreadyPollingForUserException {
		this.gmailRepository = gmailRepository;
		this.messageBroker = messageBroker;

		if (userAuthCode != null && !userAuthCode.equals(""))
			service = getGmailServiceFromAuthCode(userAuthCode, userEmail); // Build a new authorized API client service.
		else {
			pollingUser = gmailRepository.findByUserId(userEmail);

			if (pollingUser == null)
				throw new UserNotFoundException("User " + userEmail + " not found in gmailRepository.");

			service = getGmailServiceFromRefreshToken(userEmail); // Build a new authorized API client service.
		}

		pollingUser = gmailRepository.findByUserId(userEmail);

		if (pollingUser.getCurrentlyPolling())
			throw new AlreadyPollingForUserException("There is already a poller running for user " + pollingUser.getUserId() + ".");
		else {
			pollingUser.setCurrentlyPolling(true);
			gmailRepository.save(pollingUser);
		}
	}

	/**
	* Build and return an authorized Gmail client service based on an auth code.
	* @return An authorized Gmail client service
 	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailServiceFromAuthCode(String userAuthCode, String userEmail) throws IOException {
		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "https://unclutter.iminsys.com";

		// Exchange auth code for access token
		InputStream in = GmailPoller.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
		GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), "https://www.googleapis.com/oauth2/v4/token", clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(), userAuthCode, REDIRECT_URI).execute();

		String accessToken = tokenResponse.getAccessToken();
		String refreshToken = tokenResponse.getRefreshToken();

		// Use access token to call API
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
		Gmail gmail = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

		// save the user information in the database
		pollingUser = gmailRepository.findByUserId(userEmail);

		if (pollingUser != null)
			pollingUser.setRefreshToken(refreshToken);
		else
			pollingUser = new GmailPollingUser(userEmail, refreshToken);

		gmailRepository.save(pollingUser);

		return gmail;
	}

	/**
	* Build and return an authorized Gmail client service based on the refresh token in the {@link repositories.GmailRepository}.
	* @return An authorized Gmail client service
 	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailServiceFromRefreshToken(String userEmail) throws IOException {
		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "https://unclutter.iminsys.com";

		// Exchange auth code for access token
		InputStream in = GmailPoller.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
		GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), pollingUser.getRefreshToken(), clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret()).execute();

		String accessToken = tokenResponse.getAccessToken();

		// Use access token to call API
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	/**
	* Runs the poller on a loop.
	* <p>
	*	A {@link java.util.concurrent.ScheduledExecutorService} will be used to schedule the poller to run with a {@link DELAY_BETWEEN_POLLS} delay.
	* </p>
	*/
	public void run() {
		try {
			pollingUser = gmailRepository.findByUserId(pollingUser.getUserId());

			if (stop) {
				System.out.println("Stopping polling for " + pollingUser.getUserId() + " (probably due to stop request).");
				pollingUser.setCurrentlyPolling(false);
				gmailRepository.save(pollingUser);
			}
			else if(pollingUser.getNumberOfEmails() > MAX_EMAILS && MAX_EMAILS != -1)
				System.out.println("Reached maximum number of emails for user " + pollingUser.getUserId());
			else {
				poll();

				final ScheduledFuture<?> pollerHandle = scheduler.schedule(this, DELAY_BETWEEN_POLLS, TimeUnit.SECONDS);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* Gets a list of emails and checks to see if the have been processed. If they have not yet been, then it extracts the raw text and creates a RawData object that is pushed to the RawDataQueue.
	*/
	public void poll() {
		try {
			// init service and list
			service = getGmailServiceFromRefreshToken(pollingUser.getUserId());
			PagableGmailMessageList pagableMessageList = listNewMessages(null);

			outerloop:
			while (pagableMessageList != null) {
				if (pollingUser.getStartOfBlockEmailId() == null) {
					pollingUser.setStartOfBlockEmailId(pagableMessageList.messages.get(0).getId());
					gmailRepository.save(pollingUser);
				}

				for (Message message : pagableMessageList.messages) {
					if (!pollingUser.getCurrentlyPolling())			
						break outerloop;

					if (message.getId().equals(pollingUser.getEndOfBlockEmailId()))
						break outerloop;

					if (pollingUser.getNumberOfEmails() >= MAX_OLD_EMAILS && MAX_OLD_EMAILS != -1 && pollingUser.getEndOfBlockEmailId() == null) {
						System.out.println("Max old emails reached for " + pollingUser.getUserId() + ". Now only checking for new emails.");
						break outerloop;
					}

					MimeMessage mimeMessage = getMimeMessage(getMessage(message.getId()));
					RawData rawData = getRawData(message.getId(), mimeMessage);
					pollingUser.incrementNumberOfEmails();

					if (rawData != null)
						addToQueue(rawData);
					
					pollingUser.setCurrentEmailId(message.getId());
					gmailRepository.save(pollingUser);
				}

				if (pagableMessageList.nextPageToken != null) {
					service = getGmailServiceFromRefreshToken(pollingUser.getUserId());
					pagableMessageList = listNewMessages(pagableMessageList.nextPageToken);
				}
				else
					pagableMessageList = null;
			}

			if (pollingUser.getStartOfBlockEmailId() != null) {
				pollingUser.setEndOfBlockEmailId(pollingUser.getStartOfBlockEmailId());
				pollingUser.setStartOfBlockEmailId(null);
				pollingUser.setCurrentEmailId(null);
				gmailRepository.save(pollingUser);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* Gets the date and time of a message and parses it into a simpler form.
	* @param email The email that's date should be returned.
	* @return The date of the email in the form of yyyy/MM/dd, e.g.: 2016/07/30
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public String getTimeStamp(MimeMessage email) throws IOException, MessagingException {
		String date = email.getHeader("Date")[0];
		String backDate = date;

		if (date.indexOf("(") != -1)
			date = date.substring(0, date.indexOf("(") - 1);

		int start = 0;

		while (start < date.length() && !Character.isDigit(date.charAt(start)))
			start++;

		if (start == date.length())
			return null;

		date = date.substring(start, date.length() - 6);

		DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		DateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate;
		String newDate = null;

		try {
			startDate = inputFormat.parse(date);
			newDate = outputFormat.format(startDate);
		} catch (ParseException e) {
			System.out.println("Incorrect date: " + backDate);
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	* Gets the date and time of a message and gets the milliseconds since Epoch.
	* @param email The email that's date should be returned.
	* @return Milliseconds elapsed since Epoch.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public long getMilliSeconds(MimeMessage email) throws IOException, MessagingException {
		String date = email.getHeader("Date")[0];
		String backDate = date;

		if (date.indexOf("(") != -1)
			date = date.substring(0, date.indexOf("(") - 1);

		int start = 0;

		while (start < date.length() && !Character.isDigit(date.charAt(start)))
			start++;

		if (start == date.length())
			return 0;

		date = date.substring(start, date.length() - 6);

		DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		Date startDate;
		long newDate = 0;

		try {
			startDate = inputFormat.parse(date);
			newDate = startDate.getTime();
		} catch (ParseException e) {
			System.out.println("Incorrect date: " + backDate);
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	* Takes a RawData object and add it to a RawDataQueue.
	* <p>
	*	If the {@link poller.GmailPollingUser#numberOfEmails} is less than {@link MAX_PRIORITY_EMAILS} then the object will be sent on the priority queue.
	* </p>
	* @param rawData The rawData object that should be added to the queue.
	*/
	public void addToQueue(RawData rawData) {
		try {
			System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + pollingUser.getUserId() + " emailCount: " + (pollingUser.getNumberOfEmails() + 1));

			if (pollingUser.getNumberOfEmails() <= MAX_PRIORITY_EMAILS)
				messageBroker.sendPriorityRawData(rawData);
			else
				messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}

	/**
	* Gets a list of posts that has to be processed.
	* <p>
	*	If the poller has previously crashed this method will be able to get only the posts that should be processed next.
	* </p>
	* @param nextPageToken the token of the page that should be looked at.
	* @return Batch message object containing all the messages found and a token to the next page.
	* @throws java.io.IOException IOException occurs.
	*/
	public PagableGmailMessageList listNewMessages(String nextPageToken) throws IOException, MessagingException {
		ListMessagesResponse response = null;
		String pageToken = null;

		// have not started processing yet
		if (pollingUser.getStartOfBlockEmailId() == null && pollingUser.getEndOfBlockEmailId() == null) {
			response = service.users().messages().list("me").execute();
		}
		//started processing old emails, but have not finished
		else if (pollingUser.getStartOfBlockEmailId() != null && pollingUser.getEndOfBlockEmailId() == null) {
			String timestamp;

			if (pollingUser.getCurrentEmailId() == null)// the email block was set but the actual processing hasnt started yet
				timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getStartOfBlockEmailId())));
			else
				timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getCurrentEmailId())));

			String query = "before:" + timestamp;
			
			if (nextPageToken == null)
				response = service.users().messages().list("me").setQ(query).execute();
			else
				response = service.users().messages().list("me").setQ(query).setPageToken(pageToken).execute();
		}
		// processing a middle block
		else if (pollingUser.getStartOfBlockEmailId() != null && pollingUser.getEndOfBlockEmailId() != null) {
			String timestamp;

			if (pollingUser.getCurrentEmailId() == null)// the email block was set but the actual processing hasnt started yet
				timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getStartOfBlockEmailId())));
			else
				timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getCurrentEmailId())));

			String query = "before:" + timestamp + " after:" + getTimeStamp(getMimeMessage(getMessage(pollingUser.getEndOfBlockEmailId())));
			
			if (nextPageToken == null)
				response = service.users().messages().list("me").setQ(query).execute();
			else
				response = service.users().messages().list("me").setQ(query).setPageToken(pageToken).execute();
		}
		// finished processing old emails and need to process new emails
		else if (pollingUser.getStartOfBlockEmailId() == null && pollingUser.getEndOfBlockEmailId() != null) {
			String timestamp;
			timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getEndOfBlockEmailId())));

			String query = "after:" + timestamp;
			
			if (nextPageToken == null)
				response = service.users().messages().list("me").setQ(query).execute();
			else
				response = service.users().messages().list("me").setQ(query).setPageToken(pageToken).execute();
		}

		List<Message> messages = new ArrayList<Message>();

		if (response.getMessages() != null) {
			messages.addAll(response.getMessages());

			if (response.getNextPageToken() != null)
				pageToken = response.getNextPageToken();
			else
				pageToken = null;
		}

		PagableGmailMessageList gbm;

		if (messages.size() == 0)
			gbm = null;
		else {
			gbm = new PagableGmailMessageList();
			gbm.messages = messages;
			gbm.nextPageToken = pageToken;
		}

		return gbm;
	}

	/**
	* Retrieve an email with a given ID.
	* @param messageId The ID of the message that should be retrieved.
	* @return MimeMessage of the message that corresponds to the given id.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
  	public Message getMessage(String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get("me", messageId).setFormat("raw").execute();

		return message;
	}

	/**
	* Get a mime version of a message that has already been retrieved as described by the JavaMail API.
	* @param message The message that has been retrieved.
	* @return MimeMessage that corresponds to the given message.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public MimeMessage getMimeMessage(Message message) throws IOException, MessagingException {
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		return email;
	}

	/**
	* Extract the text from and email and parse it as an RawData object.
	* @param msgId The ID of the message has been retrieved.
	* @param email The email that has been retrieved.
	* @return Object that contains the details of the email or null if no data is found.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public RawData getRawData(String msgId, MimeMessage email) throws IOException, MessagingException {
		if (email.getContent() == null)
			return null;

		String address;

		if (email.getSender() == null)
			address = email.getHeader("From")[0];
		else
			address = ((InternetAddress)email.getSender()).getAddress();

		if (address.indexOf("@") == -1)
			return null;

		String senderName = address.substring(0, address.indexOf("@"));

		if ( senderName.contains("news") || senderName.contains("info"))
			return null;

		String body = "";
		ArrayList<String> rawDataElements = new ArrayList<>();

		if (!email.getHeader("Subject")[0].equals(""))
			rawDataElements.add(email.getHeader("Subject")[0]);


		if (email.getContent() instanceof String) {
			String newText = extractText((String)email.getContent());

			if (newText != null && !body.contains(newText))
					body += " " + newText;
		}
		else if (email.getContent() instanceof MimeMultipart) {
			MimeMultipart emailMultiPart = (MimeMultipart) email.getContent();
			Stack<MimeMultipart> mimeStack = new Stack<>();
			mimeStack.push(emailMultiPart);

			while (!mimeStack.isEmpty()) {
				MimeMultipart mimeMultiPart = mimeStack.pop();
				if (mimeMultiPart == null)
					continue;

				for (int i = 0; i < mimeMultiPart.getCount(); i++) {
					MimeBodyPart mimeBodyPart = (MimeBodyPart)mimeMultiPart.getBodyPart(i);

					if (mimeBodyPart.getContent() == null) {
						continue;
					}
					if (mimeBodyPart.getContent() instanceof MimeMultipart) {
						MimeMultipart mmp = (MimeMultipart)mimeBodyPart.getContent();
						mimeStack.push(mmp);
					}
					else if (mimeBodyPart.getContent() instanceof String) {
						if (!((String)mimeBodyPart.getContent()).equals("")) {
							if (mimeBodyPart.isMimeType("text/plain"))
								body += (String)mimeBodyPart.getContent() + "\n";
						}
					}
				}
			}
		}

		if (body == null || body.equals(""))
			return null;

		rawDataElements.add(body);

		List<String> involvedContacts = new ArrayList<>();

		for (String contact : email.getHeader("From")) {
			int posStart = 0;
			int posEnd = contact.indexOf("<");

			if (posEnd > 0) {
				while (contact.charAt(posStart) == ' ' || contact.charAt(posStart) == '\"')
					posStart++;

				while (contact.charAt(posEnd - 1) == ' ' || contact.charAt(posEnd - 1) == '\"')
					posEnd--;

				contact = contact.substring(posStart, posEnd);

				if (!contact.equals(""))
					involvedContacts.add(contact);
			}
		}

		if (email.getHeader("Cc") != null)
			for (String contact : email.getHeader("Cc")) {
				int posStart = 0;
				int posEnd = contact.indexOf("<");

				if (posEnd > 0) {
					while (contact.charAt(posStart) == ' ' || contact.charAt(posStart) == '\"')
						posStart++;

					while (contact.charAt(posEnd - 1) == ' ' || contact.charAt(posEnd - 1) == '\"')
						posEnd--;

					contact = contact.substring(posStart, posEnd);

					if (!contact.equals(""))
						involvedContacts.add(contact);
				}
			}

		for (String contact : involvedContacts) {
			System.out.println("Contact: " + contact);
		}

		RawData rawData = new RawData("gmail", pollingUser.getUserId(), involvedContacts, msgId, rawDataElements.toArray(new String[0]), getMilliSeconds(email));

		return rawData;
	}

	/**
	* Extracts text from a string even if it contains HTML.
	* @param bodyS The string that has to be parsed.
	* @return The text contained in the provided string with HTML stripped away.
	*/
	private String extractText(String bodyS) {
		boolean isHTML = false;
		String body = "";
		List<String> elementsToProcess = new ArrayList<>();
		elementsToProcess.add("h1");
		elementsToProcess.add("h2");
		elementsToProcess.add("h3");
		elementsToProcess.add("dnf");
		elementsToProcess.add("pre");
		elementsToProcess.add("stong");

		Whitelist wl = Whitelist.none();

		for (String element : elementsToProcess)
			wl.addTags(element);

		String bodySTmp = Jsoup.clean(bodyS, wl);
		Document doc = Jsoup.parse(bodySTmp);

		String extracted = extractNodeText(doc, elementsToProcess);

		if (extracted == null || extracted.equals(""))
			extracted = doc.text();

		if (extracted.length() != 0 && !body.contains(extracted))
			body += extracted + "\n";

		body = body.replaceAll("[\\t\\n\\r]"," ");
		body = body.replaceAll("[\\s]+", " ");
		return body;
	}

	/**
	* Recursively traverses the {@link org.jsoup.nodes.Element} parsed by JSOUP and extracts the text contained in the specified HTML elements.
	* @param element The element that has to be traversed. Usually it starts with {@link org.jsoup.nodes.Document}.
	* @param elementsToProcess A list of elements whos text should be extract.
	* @return The text contained in the specified elements.
	* @see <a href="https://jsoup.org/">https://jsoup.org/</a>
	*/
	private String extractNodeText(Element element, List<String> elementsToProcess) {
		if (elementsToProcess.contains(element.tag().getName()))
			return element.text();

		String completeInnerText = "";

		for (Element child : element.children()) {
			completeInnerText += extractNodeText(child, elementsToProcess);
		}

		return completeInnerText;
	}

	/**
	* Prints the email to the screen. Used for debugging.
	* @param message The email that needs to be printed.
	*/
	public void printEmail(Message message) {
		try {
			System.out.println(message.toPrettyString());
		}
		catch (IOException ioe) {
			System.out.println("COULD NOT PRINT EMAIL");
			ioe.printStackTrace();
		}
	}

	/**
	* Prints the email to the screen. Used for debugging.
	* @param message The email that needs to be printed.
	*/
	public void printEmail(MimeMessage message) {
		System.out.println(message);
	}

	/**
	* Stop the poller.
	*/
	public void stopPoller() {
		stop = true;
	}

	/**
	* Get the value of userEmail.
	* @return The email address of the user for which this poller is polling for.
	*/
	public String getUserId() {
		return pollingUser.getUserId();
	}
}
