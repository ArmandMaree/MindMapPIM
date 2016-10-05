package poller;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;

import org.apache.commons.codec.binary.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

import data.*;
import repositories.*;
import com.unclutter.poller.*;

/**
* Uses the Gmail API to retrieve new emails and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class GmailPoller implements Poller {
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
	private String userAuthCode;
	private Gmail service = null;
	private final String userId = "me";
	private String lastEmailTimeStampDate = "1970/01/01";
	private long lastEmailMilli = 0;
	private boolean stop = false;
	private String newestEmailId = "";
	private String lastId = "";
	private String refreshToken;
	private GmailRepository gmailRepository;
	private String userEmail = "";
	private boolean processedOldEmails = false;
	private Properties props;
	private MessageBroker messageBroker;
	private boolean firstPageDone = false;
	private boolean oldDone = false;
	private long MAX_OLD_EMAILS = 200;
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
	public GmailPoller(GmailRepository gmailRepository, MessageBroker messageBroker, String userAuthCode, String userEmail) throws IOException, UserNotFoundException {
		this.gmailRepository = gmailRepository;
		this.messageBroker = messageBroker;
		this.userAuthCode = userAuthCode;
		this.userEmail = userEmail;

		if (userAuthCode != null && !userAuthCode.equals(""))
			service = getGmailServiceFromAuthCode(); // Build a new authorized API client service.
		else {
			pollingUser = gmailRepository.findByUserId(userEmail);

			if (pollingUser == null)
				throw new UserNotFoundException("User " + userEmail + " not found in gmailRepository.");

			service = getGmailServiceFromRefreshToken(); // Build a new authorized API client service.
		}
	}

	/**
	* Build and return an authorized Gmail client service based on an auth code.
	* @return An authorized Gmail client service
 	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailServiceFromAuthCode() throws IOException {
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
	public Gmail getGmailServiceFromRefreshToken() throws IOException {
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
	*/
	public void run() {
		try {
			if (service == null)
				throw new GmailServiceNotSetException("Tried to start polling for user " + pollingUser.getUserId() + " but the Gmail Service was not set.");

			pollingUser = gmailRepository.findByUserId(pollingUser.getUserId());

			if (pollingUser.getCurrentlyPolling())
				throw new AlreadyPollingForUserException("There is already a poller running for user " + pollingUser.getUserId() + ".");
			else {
				pollingUser.setCurrentlyPolling(true);
				gmailRepository.save(pollingUser);
			}

			while (!stop) {
				poll();
				pollingUser = gmailRepository.findByUserId(userEmail);

				if (!pollingUser.getCurrentlyPolling()) {
					System.out.println("Poller stopping for " + userEmail + " due to no refreshToken (probably due to stop request).");
					pollingUser.setCurrentlyPolling(false);
					gmailRepository.save(pollingUser);
					return;
				}

				try {
					java.lang.Thread.sleep(60 * 1000);
				}
				catch (InterruptedException ie) {
					ie.printStackTrace();
				}
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
			service = getGmailServiceFromRefreshToken();
			System.out.println("START");
			System.out.println("Start: " + pollingUser.getStartOfBlockEmailId());
			System.out.println("Current: " + pollingUser.getCurrentEmailId());
			System.out.println("End: " + pollingUser.getEndOfBlockEmailId());
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
						System.out.println("Max old emails reached for " + userEmail + ". Now only checking for new emails.");
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
					service = getGmailServiceFromRefreshToken();
					System.out.println("NEXT RUN");
					System.out.println("Start: " + pollingUser.getStartOfBlockEmailId());
					System.out.println("Current: " + pollingUser.getCurrentEmailId());
					System.out.println("End: " + pollingUser.getEndOfBlockEmailId());
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
	* Gets the date and time of a message and parses it into a simper form.
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
	* @param rawData The rawData object that should be added to the queue.
	*/
	public void addToQueue(RawData rawData) {
		try {
			System.out.println("Sending RawData: " + rawData.getPimItemId() + " for user: " + userEmail + " firstPageDone: " + firstPageDone + " oldDone: " + oldDone);

			if (!firstPageDone || oldDone)
				messageBroker.sendPriorityRawData(rawData);
			else
				messageBroker.sendRawData(rawData);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}

	/**
	* List all the emails after a specific date on a specific page.
	* @param nextPageToken the token of the page that should be looked at.
	* @return Batch message object containing all the messages found and a token to the next page.
	* @throws java.io.IOException IOException occurs.
	*/
	public PagableGmailMessageList listNewMessages(String nextPageToken) throws IOException, MessagingException {
		ListMessagesResponse response = null;
		String pageToken = null;

		// have not started processing yet
		if (pollingUser.getStartOfBlockEmailId() == null && pollingUser.getEndOfBlockEmailId() == null) {
			response = service.users().messages().list(userId).execute();
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
				response = service.users().messages().list(userId).setQ(query).execute();
			else
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
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
				response = service.users().messages().list(userId).setQ(query).execute();
			else
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
		}
		// finished processing old emails and need to process new emails
		else if (pollingUser.getStartOfBlockEmailId() == null && pollingUser.getEndOfBlockEmailId() != null) {
			String timestamp;
			timestamp = getTimeStamp(getMimeMessage(getMessage(pollingUser.getEndOfBlockEmailId())));

			String query = "after:" + timestamp;
			
			if (nextPageToken == null)
				response = service.users().messages().list(userId).setQ(query).execute();
			else
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
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
	* Retrieve an email.
	* @param messageId The ID of the message that should be retrieved.
	* @return MimeMessage of the message that corresponds to the given id.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
  	public Message getMessage(String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

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

		RawData rawData = new RawData("gmail", userEmail, involvedContacts, msgId, rawDataElements.toArray(new String[0]), getMilliSeconds(email));

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
}
