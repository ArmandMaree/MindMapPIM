package listeners;

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

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;

import data.*;
import poller.*;
import repositories.*;

import java.util.*;
import java.io.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the frontend service.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class FrontendListener {
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

	private final String itemResponseQueueName = "item-response.frontend.rabbit";
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private GmailRepository gmailRepository;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public void receiveItemRequest(ItemRequestIdentified itemRequestIdentified) throws IOException {
		List<String> items = new ArrayList<>();

		Gmail service = getGmailServiceFromRefreshToken(itemRequestIdentified.getUserId());

		for (String itemId : itemRequestIdentified.getItemIds()) {
			try {
				MimeMessage email = getMimeMessage(getMessage(itemId, service));
				String body = "";

				if (email.getContent() instanceof String) {
					body += (String)email.getContent();
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
									String tmpBody = (String)mimeBodyPart.getContent() + "\n";

									if (!body.contains(tmpBody))
										body += tmpBody;
								}
							}
						}
					}
				}

				items.add(body);
			}
			catch (Exception ignore) {}
		}

		ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), items.toArray(new String[items.size()]));
		rabbitTemplate.convertAndSend(itemResponseQueueName, itemResponseIdentified);
	}

	/**
	* Build and return an authorized Gmail client service based on an auth code.
	* @return An authorized Gmail client service
	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailServiceFromRefreshToken(String userEmail) throws IOException {
		PollingUser pollingUser = gmailRepository.findByUserId(userEmail);
		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "https://bubbles.iminsys.com";

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
	* Retrieve an email.
	* @param messageId The ID of the message that should be retrieved.
	* @return MimeMessage of the message that corresponds to the given id.
	* @throws java.io.IOException IOException occurs.
	* @throws javax.mail.MessagingException Error retrieving email.
	*/
	public Message getMessage(String messageId, Gmail service) throws IOException, MessagingException {
		Message message = service.users().messages().get("me", messageId).setFormat("raw").execute();

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
		byte[] emailBytes = Base64.decodeBase64(message.getRaw());
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

		return email;
	}
}
