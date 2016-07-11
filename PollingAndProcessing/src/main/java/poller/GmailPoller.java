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

public class GmailPoller implements Poller {
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

	private RawDataQueue rawQueue;
	private String userAuthCode;
	private Gmail service;
	private final String userId = "me";
	private String lastEmailTimeStampDate = "";
	private long lastEmailMilli = 0;
	private boolean stop = false;
	private String firstId = "";

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public GmailPoller(RawDataQueue rawQueue, String userAuthCode) {
		this.rawQueue = rawQueue;
		this.userAuthCode = userAuthCode;

		try {
			service = getGmailService(); // Build a new authorized API client service.
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void run() {
		while (!stop) {
			poll();

			try {
				java.lang.Thread.sleep(10000);
			}
			catch (InterruptedException ignore) {}
		}
	}

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

					if (getMilliSeconds(message.getId()) <= lastEmailMilli)
						continue;

					if (lastEmailDate == null) {
						lastEmailDate = getTimeStamp(message.getId());
						tmpMilli = getMilliSeconds(message.getId());
						firstId = message.getId();
					}

					RawData rawData = getRawData(message.getId());

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

	public String getTimeStamp(String messageId) throws IOException, MessagingException {
		MimeMessage email = getMimeMessage(messageId);
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

	public long getMilliSeconds(String messageId) throws IOException, MessagingException {
		MimeMessage email = getMimeMessage(messageId);
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

	public void addToQueue(RawData data) {
		try {
			rawQueue.put(data);
		}
		catch (InterruptedException ex) {
			System.out.println("Interrupted while waiting");
		}
	}

	/**
	 * Build and return an authorized Gmail client service.
	 * @return an authorized Gmail client service
	 * @throws IOException
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

  	public MimeMessage getMimeMessage(String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
		// System.out.println(message.toPrettyString()); // print raw message
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		return email;
	}

	public MimeMessage getMimeMessage(Message message) throws IOException, MessagingException {
		// System.out.println(message.toPrettyString()); // print raw message
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		return email;
	}

	public RawData getRawData(String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
		MimeMessage email = getMimeMessage(message);
		String body = "";
		String[] elementsToProcess = {"p", "pre", "td", "h1", "h2", "h3"};
		ArrayList<String> rawDataElements = new ArrayList<>();

		if (!email.getHeader("Subject")[0].equals(""))
			rawDataElements.add(email.getHeader("Subject")[0]);

		if (email.getContent() == null)
			return null;

		boolean isHTML = false;

		if (email.getContent() instanceof String) {
			String bodyS = (String)email.getContent();

			if (bodyS == null || bodyS.length() == 0)
				return null;

			if (bodyS.charAt(0) == '<') {
				for (int i = bodyS.length() - 1; i > -1; i--) {
					if (bodyS.charAt(i) == '>') {
						isHTML = true;
						break;
					}
					else if ((int)bodyS.charAt(i) != 10 && (int)bodyS.charAt(i) != 13 && (int)bodyS.charAt(i) != 9 && (int)bodyS.charAt(i) != 32)
						break;
				}
			}

			if (isHTML) {
				Document doc = Jsoup.parse(bodyS);

				for (String elem : elementsToProcess) {
					Elements elements = doc.select(elem);

					for (Element element : elements) {
						if (element.text().replaceAll("[\\t\\n\\r]"," ").length() != 0) {
							body += element.text().replaceAll("[\\t\\n\\r]"," ");

							if (body.charAt(body.length() - 1) != '.')
								body += element.text().replaceAll("[\\t\\n\\r]"," ") + ".\n";
							else
								body += element.text().replaceAll("[\\t\\n\\r]"," ") + "\n";
						}
					}
				}
			}
			else
				body += bodyS.replaceAll("[\\t\\n\\r]"," ");
		}
		else if (email.getContent() instanceof MimeMultipart) {
			MimeMultipart emailMultiPart = (MimeMultipart) email.getContent();
			Stack<MimeBodyPart> mimeStack = new Stack<>();

			for (int i = emailMultiPart.getCount() - 1; i >= 0; i--) {
				MimeBodyPart bodyPart = (MimeBodyPart)emailMultiPart.getBodyPart(i);

				if (bodyPart.getContent() instanceof String) {
					String bodyS = (String)bodyPart.getContent();

					if (bodyS == null || bodyS.length() == 0)
						continue;

					if (bodyS.charAt(0) == '<') {
						for (int j = bodyS.length() - 1; j > -1; j--) {
							if (bodyS.charAt(j) == '>') {
								isHTML = true;
								break;
							}
							else if ((int)bodyS.charAt(j) != 10 && (int)bodyS.charAt(j) != 13 && (int)bodyS.charAt(j) != 9 && (int)bodyS.charAt(j) != 32)
								break;
						}
					}

					if (isHTML) {
						Document doc = Jsoup.parse(bodyS);
						//System.out.println(doc.body().text());
						body = doc.body().text();

						/*for (String elem : elementsToProcess) {
							Elements elements = doc.select(elem);

							for (Element element : elements) {
								if (element.text().replaceAll("[\\t\\n\\r]"," ").length() != 0) {
									body += element.text().replaceAll("[\\t\\n\\r]"," ");

									if (body.charAt(body.length() - 1) != '.')
										body += element.text().replaceAll("[\\t\\n\\r]"," ") + ".\n";
									else
										body += element.text().replaceAll("[\\t\\n\\r]"," ") + "\n";
								}
							}
						}*/
					}
					else
						body += bodyS.replaceAll("[\\t\\n\\r]"," ");
				}
			}
		}

		rawDataElements.add(body);
		RawData rawData = new RawData();
		rawData.pimSource = "Gmail";
		rawData.userId = "";
		rawData.pimItemId = message.getId();
		rawData.data = rawDataElements.toArray(new String[0]);

		return rawData;
		// return null;
	}

	public void printEmail(Message message) {
		try {
			System.out.println(message.toPrettyString());
		}
		catch (IOException ioe) {
			System.out.println("COULD NOT PRINT EMAIL");
			ioe.printStackTrace();
		}
	}
}
