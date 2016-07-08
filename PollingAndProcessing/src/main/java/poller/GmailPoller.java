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
	/** Application name. */
	private static final String APPLICATION_NAME = "Gmail API";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
		System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY =
		JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/gmail-java-quickstart.json
	 */
	private static final List<String> SCOPES =
		Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	private RawDataQueue queue;
	private String userAuthCode;

	public GmailPoller(RawDataQueue queue, String userAuthCode) {
		this.queue = queue;
		this.userAuthCode = userAuthCode;


		try {
			// Build a new authorized API client service.
			Gmail service = getGmailService();

			// Print the labels in the user's account.
			String user = "me";
			listLabels(service, user);

			// Print messages in user's account
			List<Message> messages = listMessagesMatchingQuery(service, user, 1);

			//Print last message
			getMessage(service, user, messages.get(0).getId());

			// Get MimeEmail
			getMimeMessage(service, user, messages.get(0).getId());

			// Print Email body
			getMessageBody(service, user, messages.get(0).getId());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		catch (MessagingException me) {
			me.printStackTrace();
			System.exit(1);
		}
	}

	public void run() {

	}

	public RawData poll() {
		//Polling to see if a new email has arrived
		return null;
	}

	public void addToQueue(RawData data) {
		try {
			queue.put(data);
		}
		catch (InterruptedException ex) {
			System.out.println("Interrupted while waiting");
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in =
			GmailPoller.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
			GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(
						HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(
			flow, new LocalServerReceiver()).authorize("user");
		System.out.println(
				"Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Gmail client service.
	 * @return an authorized Gmail client service
	 * @throws IOException
	 */
	public Gmail getGmailService() throws IOException {
		//Credential credential = authorize();

		// (Receive authCode via HTTPS POST)

		// Set path to the Web application client_secret_*.json file you downloaded from the
		// Google Developers Console: https://console.developers.google.com/apis/credentials?project=_
		// You can also find your Web application client ID and client secret from the
		// console and specify them directly when you create the GoogleAuthorizationCodeTokenRequest
		// object.
		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "http://codehaven.co.za";

		// Exchange auth code for access token
		InputStream in = GmailPoller.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets =
			GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));
			GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
				new NetHttpTransport(),
			  	JacksonFactory.getDefaultInstance(),
			  	"https://www.googleapis.com/oauth2/v4/token",
			  	clientSecrets.getDetails().getClientId(),
			  	clientSecrets.getDetails().getClientSecret(),
			  	userAuthCode,
			  	REDIRECT_URI)  // Specify the same redirect URI that you use with your web
			              // app. If you don't have a web version of your app, you can
			              // specify an empty string.
			  	.execute();

		String accessToken = tokenResponse.getAccessToken();
		//System.out.println("\n\nACCESS TOKEN: " + accessToken + "\n");

		// Use access token to call API
		GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);


		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	/*public static void main(String[] args) throws IOException {
		try {
			// Build a new authorized API client service.
			Gmail service = getGmailService();

			// Print the labels in the user's account.
			String user = "me";
			listLabels(service, user);

			// Print messages in user's account
			List<Message> messages = listMessagesMatchingQuery(service, user, 1);

			//Print last message
			getMessage(service, user, messages.get(0).getId());

			// Get MimeEmail
			getMimeMessage(service, user, messages.get(0).getId());

			// Print Email body
			getMessageBody(service, user, messages.get(0).getId());
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		catch (MessagingException me) {
			me.printStackTrace();
			System.exit(1);
		}
	}*/

	public List<Label> listLabels(Gmail service, String userId) throws IOException  {
		ListLabelsResponse listResponse = service.users().labels().list(userId).execute();
		List<Label> labels = listResponse.getLabels();

		if (labels.size() == 0) {
			System.out.println("No labels found.");
		} else {
			System.out.println("Labels:");
			for (Label label : labels) {
				System.out.printf("- %s\n", label.getName());
			}
		}

		return labels;
	}

	public List<Message> listMessagesMatchingQuery(Gmail service, String userId, long limit) throws IOException {
		ListMessagesResponse response = service.users().messages().list(userId).setMaxResults(limit).execute();
		List<Message> messages = new ArrayList<Message>();

		if (response.getMessages() != null) {
			messages.addAll(response.getMessages());
		}

		if (messages.size() == 0) {
			System.out.println("\nNo messages found.");
		} else {
			System.out.println("\nMessage id:");

			for (Message message : messages) {
				System.out.println(message.getId());
			}
		}

		return messages;
	}

	public Message getMessage(Gmail service, String userId, String messageId) throws IOException {
		Message message = service.users().messages().get(userId, messageId).execute();
		// System.out.println("Message snippet: " + message.getSnippet());
		//System.out.println(message.toPrettyString());

		return message;
  	}

  	public MimeMessage getMimeMessage(Gmail service, String userId, String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		System.out.println("\nMessage Date:");
		System.out.println(email.getHeader("Date")[0]);

		System.out.println("\nMessage Subject:");
		System.out.println(email.getHeader("Subject")[0]);

		return email;
	}

	public String getMessageBody(Gmail service, String userId, String messageId) throws IOException, MessagingException {
		Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();
		System.out.println("\nMessage Body:");
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
		String body = "";
		String[] elementsToProcess = {"p", "pre", "td", "h1", "h2", "h3"};

		if (email.getContent() == null) {
			return body;
		}

		boolean isHTML = false;

		if (email.getContent() instanceof String) {
			String bodyS = (String)email.getContent();

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
				else System.out.println("UNKNOWN MIME (NOT STRING)\n");
			}
		}
		else System.out.println("UNKNOWN MIME (NOT MIMEMULTIPART)\n");

		System.out.println(body);
		return body;
	}
}
