package poller;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
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

import org.apache.commons.codec.binary.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.LinkedBlockingQueue;

import data.*;

/**
* Uses the Gmail API to retrieve new emails and add them to a queue that lets them be processed.
*
* @author  Armand Maree
* @since   2016-07-11
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
	private Gmail service;
	private final String userId = "me";
	private String lastEmailTimeStampDate = "";
	private long lastEmailMilli = 0;
	private boolean stop = false;
	private String firstId = "";

	@Autowired
	private RabbitTemplate rabbitTemplate;

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
	* @param rabbitTemplate Reference to a rabbitTemplate used to communicate with a RabbitMQ server.
	* @param userAuthCode Authentication code received from the login service.
	*/
	public GmailPoller(String userAuthCode) {
		this.userAuthCode = userAuthCode;

		try {
			service = getGmailService(); // Build a new authorized API client service.
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	* Runs the poller on a loop.
	*/
	public void run() {
		while (!stop) {
			poll();

			try {
				java.lang.Thread.sleep(10000);
			}
			catch (InterruptedException ignore) {}
		}
	}

	/**
	* Gets a list of emails and checks to see if the have been processed. If they have not yet been, then it extracts the raw text and creates a RawData object that is pushed to the RawDataQueue.
	*/
	public void poll() {
		try {
			GmailBatchMessages gbm = listNewMessages(null);
			String lastEmailDate = null;
			long tmpMilli = 0;

			while (gbm != null) {
				if (firstId.equals(gbm.messages.get(0).getId()))
					break;

				for (Message message : gbm.messages) {
					if (firstId.equals(message.getId()))
						break;

					Message msg = getMessage(message.getId());
					MimeMessage mimeMessage = getMimeMessage(msg);

					if (getMilliSeconds(mimeMessage) <= lastEmailMilli)
						continue;

					if (lastEmailDate == null) {
						lastEmailDate = getTimeStamp(mimeMessage);
						tmpMilli = getMilliSeconds(mimeMessage);
						firstId = message.getId();
					}

					RawData rawData = getRawData(message.getId(), mimeMessage);

					if (rawData != null)
						addToQueue(rawData);
				}

				gbm = listNewMessages(gbm.nextPageToken);
			}

			if (lastEmailDate != null) {
				lastEmailTimeStampDate = lastEmailDate;
				lastEmailMilli = tmpMilli;
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (MessagingException me) {
			me.printStackTrace();
		}
	}

	/**
	* Gets the date and time of a message and parses it into a simper form.
	* @param messageId The ID of the message that's date should be returned.
	* @return The date of the email in the form of yyyy/MM/dd, e.g.: 2016/07/30
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public String getTimeStamp(MimeMessage email) throws IOException, MessagingException {
		String date = email.getHeader("Date")[0];

		if (date.indexOf("(") != -1)
			date = date.substring(0, date.indexOf("(") - 1);

		date = date.substring(5, date.length() - 6);

		DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		DateFormat outputFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date startDate;
		String newDate = null;

		try {
			startDate = inputFormat.parse(date);
			newDate = outputFormat.format(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return newDate;
	}

	/**
	* Gets the date and time of a message and gets the milliseconds since Epoch.
	* @param messageId The ID of the message that's date should be returned.
	* @return Milliseconds elapsed since Epoch.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public long getMilliSeconds(MimeMessage email) throws IOException, MessagingException {
		String date = email.getHeader("Date")[0];

		if (date.indexOf("(") != -1)
			date = date.substring(0, date.indexOf("(") - 1);

		date = date.substring(5, date.length() - 6);

		DateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		Date startDate;
		long newDate = 0;

		try {
			startDate = inputFormat.parse(date);
			newDate = startDate.getTime();
		} catch (ParseException e) {
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
			rabbitTemplate.convertAndSend(rawDataQueue, rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not connect to RabbitMQ.");
		}
	}

	/**
	* Build and return an authorized Gmail client service.
	* @return An authorized Gmail client service
 	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailService() throws IOException {
		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "http://codehaven.co.za";

		// Exchange auth code for access token
		InputStream in = GmailPoller.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
		GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), "https://www.googleapis.com/oauth2/v4/token", clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret(), userAuthCode, REDIRECT_URI).execute();

		String accessToken = tokenResponse.getAccessToken();

		// Use access token to call API
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);

		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	/**
	* List all the emails after a specific date on a specific page.
	* @param nextPageToken the token of the page that should be looked at.
	* @return Batch message object containing all the messages found and a token to the next page.
	* @throws java.io.IOException IOException occurs.
	*/
	public GmailBatchMessages listNewMessages(String nextPageToken) throws IOException {
		//ListMessagesResponse response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
		String query = "";
		ListMessagesResponse response = null;
		String pageToken = null;

		if (!lastEmailTimeStampDate.equals("")) {
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

		GmailBatchMessages gbm ;

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
		// System.out.println(message.toPrettyString()); // print raw message

		return message;
	}

	/**
	* Get a mime version of a message that has already been retrieved as described by the JavaMail API.
	* @param message The message that needs to be retrieved.
	* @return MimeMessage that corresponds to the given message.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public MimeMessage getMimeMessage(Message message) throws IOException, MessagingException {
		// System.out.println(message.toPrettyString()); // print raw message
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		return email;
	}

	/**
	* Extract the text from and email and parse it as an RawData object.
	* @param messageId The ID of the message that should be retrieved.
	* @return Object that contains the details of the email or null if no data is found.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public RawData getRawData(String msgId, MimeMessage email) throws IOException, MessagingException {
		// printEmail(service.users().messages().get(userId, messageId).setFormat("full").execute());
		String body = "";
		String[] elementsToProcess = {"p", "pre", "td", "h1", "h2", "h3"};
		ArrayList<String> rawDataElements = new ArrayList<>();

		if (!email.getHeader("Subject")[0].equals(""))
			rawDataElements.add(email.getHeader("Subject")[0]);

		if (email.getContent() == null) {
			return null;
		}

		boolean isHTML = false;

		if (email.getContent() instanceof String) {
			body += extractText((String)email.getContent());
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
						String tmpBody = extractText((String)mimeBodyPart.getContent()) + "\n";

						if (!body.contains(tmpBody))
							body += tmpBody;
					}
					// else
					// 	System.out.println("UNKNOWN MIME TYPE 1: " + mimeBodyPart.getContent());
				}
			}
		}
		// else
		// 	System.out.println("UNKNOWN MIME TYPE 2: " + email.getContent());

		rawDataElements.add(body);
		String userId = email.getHeader("Delivered-To")[0];
		ArrayList<String> involvedContacts = new ArrayList<>();
		involvedContacts.add(email.getHeader("Delivered-To")[0]);
		RawData rawData = new RawData("Gmail", userId, involvedContacts.toArray(new String[0]), msgId, rawDataElements.toArray(new String[0]), getMilliSeconds(email));

		return rawData;
	}

	/**
	* Extracts text from a string even if it contains HTML.
	* @param bodyS The string that has to be parsed.
	*/
	private static String extractText(String bodyS) {
		boolean isHTML = false;
		String[] elementsToProcess = {"p", "pre", "td", "h1", "h2", "h3"};
		String body = "";

		if (bodyS.charAt(0) == '<') { // if starts and ends with angle bracket then it is HTML
			for (int i = bodyS.length() - 1; i > -1; i--) {
				if (bodyS.charAt(i) == '>') {
					isHTML = true;
					break;
				}
				else if ((int)bodyS.charAt(i) != 10 && (int)bodyS.charAt(i) != 13 && (int)bodyS.charAt(i) != 9 && (int)bodyS.charAt(i) != 32)
					break;
			}
		}

		if (isHTML) { // if it is HTML then use Jsoup to parse
			Document doc = Jsoup.parse(bodyS);

			for (String elem : elementsToProcess) {
				Elements elements = doc.select(elem);

				for (Element element : elements) {
					String replaced = element.text().replaceAll("[\\t\\n\\r]"," ");

					if (replaced.length() != 0 && !body.contains(replaced))
						body += replaced + "\n";
				}
			}
		}
		else {
			String replaced = bodyS.replaceAll("[\\t\\n\\r]"," ");

			if (replaced.length() != 0 && !body.contains(replaced))
				body += replaced + "\n";
		}

		body = body.replaceAll("[\\t\\n\\r]"," ");
		body = body.replaceAll("[\\s]+", " ");
		return body;
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

	public void printEmail(MimeMessage message) {
		System.out.println(message);
	}
}
