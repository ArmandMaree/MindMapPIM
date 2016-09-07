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

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.AmqpException;

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

/**
* Uses the Gmail API to retrieve new emails and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class GmailPoller implements Poller {
	final static String rawDataQueue = "raw-data.processing.rabbit";
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);
	private String userAuthCode;
	private Gmail service = null;
	private final String userId = "me";
	private String lastEmailTimeStampDate = "1970/01/01";
	private long lastEmailMilli = 0;
	private boolean stop = false;
	private String firstId = "";
	private String lastId = "";
	private String refreshToken;
	private GmailRepository gmailRepository;
	private String userEmail = "";
	private boolean processedOldEmails = false;
	private GmailPollingUser pollingUser;
	private Properties props;
	private RabbitTemplate rabbitTemplate;
	private boolean firstPageDone = false;
	private boolean oldDone = false;
	private long maxEmails = 200;
	private long currNumEmails = 0;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	* Constructor that initializes some fields and starts up a Gmail service.
	* @param gmailRepository The repository where all the users and their refresh tokens are stored.
	* @param rabbitTemplate Reference to a rabbitTemplate used to communicate with a RabbitMQ server.
	* @param userAuthCode Authentication code received from the login service.
	* @param userEmail The email address of the user.
	*/
	public GmailPoller(GmailRepository gmailRepository, RabbitTemplate rabbitTemplate, String userAuthCode, String userEmail) {
		this.gmailRepository = gmailRepository;
		this.rabbitTemplate = rabbitTemplate;
		this.userAuthCode = userAuthCode;
		this.userEmail = userEmail;

		try {
			if (userAuthCode != null && !userAuthCode.equals(""))
				service = getGmailServiceFromAuthCode(); // Build a new authorized API client service.
			else {
				GmailPollingUser pollingUser = gmailRepository.findByUserId(userEmail);

				if (pollingUser == null)
					return;

				service = getGmailServiceFromRefreshToken(); // Build a new authorized API client service.
				firstPageDone = true;
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		pollingUser = gmailRepository.findByUserId(userEmail);
	}

	/**
	* Set the id of firstId.
	* @param firstId the most recent email ID that has been received.
	*/
	public void setFirstId(String firstId) {
		this.firstId = firstId;
	}

	/**
	* Set the value of lastEmailTimeStampDate.
	* @param lastEmailTimeStampDate The timestamp of the earliest email that has been received. If this value is "DONE" then it means all old emails have been received already.
	*/
	public void setLastDate(String lastEmailTimeStampDate) {
		if (lastEmailTimeStampDate.equals("DONE"))
			processedOldEmails = true;

		this.lastEmailTimeStampDate = lastEmailTimeStampDate;
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
		refreshToken = tokenResponse.getRefreshToken();

		// Use access token to call API
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
		Gmail gmail = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

		GmailPollingUser pollingUser = gmailRepository.findByUserId(userEmail);

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
		GmailPollingUser pollingUser = gmailRepository.findByUserId(userEmail);
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
		if (service == null)
			return;

		while (!stop) {
			if (currNumEmails >= maxEmails && maxEmails != -1){
				System.out.println("Max emails reached for user: " + userEmail);
				return;
			}

			GmailPollingUser pollingUser = gmailRepository.findByUserId(userEmail);

			if (pollingUser.getRefreshToken() == null) {
				System.out.println("Poller stopping for user: " + userEmail);
				return;
			}
			else
				refreshToken = pollingUser.getRefreshToken();

			poll();
			oldDone = true;

			try {
				java.lang.Thread.sleep(60 * 1000);
			}
			catch (InterruptedException ignore) {}
		}
	}

	/**
	* Gets a list of emails and checks to see if the have been processed. If they have not yet been, then it extracts the raw text and creates a RawData object that is pushed to the RawDataQueue.
	*/
	public void poll() {
		try {
			service = getGmailServiceFromRefreshToken();
			String tmpFirst = null;
			GmailBatchMessages gbm = listNewMessages(null);

			while (gbm != null) {
				if (tmpFirst == null)
					tmpFirst = gbm.messages.get(0).getId();

				for (Message message : gbm.messages) {
					if (firstId.equals(message.getId())) {
						break;
					}

					Message msg = getMessage(message.getId());
					MimeMessage mimeMessage = getMimeMessage(msg);

					if (!processedOldEmails) {
						lastEmailTimeStampDate = getTimeStamp(mimeMessage);
						pollingUser.setLastEmail(lastEmailTimeStampDate);
						gmailRepository.save(pollingUser);
					}

					RawData rawData = getRawData(message.getId(), mimeMessage);
					currNumEmails++;

					if (rawData != null)
						addToQueue(rawData);
				}

				firstPageDone = true;

				if (gbm.nextPageToken != null) {
					service = getGmailServiceFromRefreshToken();
					gbm = listNewMessages(gbm.nextPageToken);
				}
				else
					gbm = null;
			}

			if (!firstId.equals(tmpFirst) && tmpFirst != null) {
				firstId = tmpFirst;
				pollingUser.setEarliestEmail(firstId);
				gmailRepository.save(pollingUser);
			}

			if (!processedOldEmails) {
				processedOldEmails = true;
				pollingUser.setLastEmail("DONE");
				gmailRepository.save(pollingUser);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (MessagingException me) {
			me.printStackTrace();
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
					rabbitTemplate.convertAndSend("priority-" + rawDataQueue, rawData);
				else
					rabbitTemplate.convertAndSend(rawDataQueue, rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
		}
	}

	/**
	* List all the emails after a specific date on a specific page.
	* @param nextPageToken the token of the page that should be looked at.
	* @return Batch message object containing all the messages found and a token to the next page.
	* @throws java.io.IOException IOException occurs.
	*/
	public GmailBatchMessages listNewMessages(String nextPageToken) throws IOException {
		String query = "";
		ListMessagesResponse response = null;
		String pageToken = null;

		if (!processedOldEmails) {
			query = "after:" + lastEmailTimeStampDate;

			if (nextPageToken == null)
				response = service.users().messages().list(userId).setQ(query).execute();
			else
				response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
		}
		else {
			if (nextPageToken == null)
				response = service.users().messages().list(userId).execute();
			else
				response = service.users().messages().list(userId).setPageToken(nextPageToken).execute();
		}

		List<Message> messages = new ArrayList<Message>();

		if (response.getMessages() != null) {
			messages.addAll(response.getMessages());

			if (response.getNextPageToken() != null)
				pageToken = response.getNextPageToken();
			else
				pageToken = null;
		}

		GmailBatchMessages gbm;

		if (messages.size() == 0)
			gbm = null;
		else {
			gbm = new GmailBatchMessages();
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
							// String newText = extractText((String)mimeBodyPart.getContent()) + "\n";

							// if (newText == null)
							// 	continue;

							// if (!body.contains(newText))
							// 	body += " " + newText;
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
