package listeners;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import data.*;
import poller.*;
import repositories.*;

import java.util.*;
import java.io.*;

import com.unclutter.poller.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* Waits for messages from the frontend service.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class ItemListener {
	private static final String APPLICATION_NAME = "Gmail API";
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static HttpTransport HTTP_TRANSPORT;
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY);

	private final String itemResponseQueueName = "item-response.frontend.rabbit";

	private MessageBroker messageBroker;
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

	/**
	* Constructor.
	* @param gmailRepository The repository that will be used to persist information of each user that is being polled for. {@link poller.GmailPollingUser}
	*/
	public ItemListener(GmailRepository gmailRepository) {
		this.gmailRepository = gmailRepository;
	}

	/**
	* Set the value of messageBroker.
	* @param messageBroker Will be passed to the pollers inorder for them to send messages.
	*/
	public void setMessageBroker(MessageBroker messageBroker) {
		this.messageBroker = messageBroker;
	}

	/**
	* Receives a request for a  list of email.
	* <p>
	*	Gets the refresh token for the specific user and uses the Gmail API to retrieve all the requested emails.
	* </p>
	* @param itemRequestIdentified Contains all the relevant user information and the IDs of the emails that needs to be retrieved.
	* @throws IOException Usually if the Gmail API rejects he refresh token.
	*/
	public void receiveItemRequest(ItemRequestIdentified itemRequestIdentified) throws IOException {
		System.out.println("Received: " + itemRequestIdentified);
		List<String> items = new ArrayList<>();

		Gmail service = getGmailServiceFromRefreshToken(itemRequestIdentified.getUserId());

		if (service == null)
			return;

		for (String itemId : itemRequestIdentified.getItemIds()) {
			if (items.size() == 0) {
				items.add(itemId);
				continue;
			}
			
			String plainText = "";
			String htmlText = "";
			String body = "";

			try {
				MimeMessage email = getMimeMessage(getMessage(itemId, service));

				if (email.getContent() instanceof String) {
					plainText += (String)email.getContent();
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

									if (!body.contains(tmpBody)) {
										body += tmpBody;

										if (mimeBodyPart.isMimeType("text/plain"))
											plainText += tmpBody;
										else
											htmlText += tmpBody;
									}
								}
							}
						}
					}
				}

				if (!htmlText.equals(""))
					items.add(htmlText);
				else
					items.add(plainText);

			}
			catch (Exception ignore) {}
		}

		ItemResponseIdentified itemResponseIdentified = new ItemResponseIdentified(itemRequestIdentified.getReturnId(), items.toArray(new String[items.size()]));
		System.out.println("Responded: " + itemResponseIdentified);

		try {
			messageBroker.sendItem(itemResponseIdentified);
		}
		catch (MessageNotSentException mnse) {
			mnse.printStackTrace();
		}
	}

	/**
	* Build and return an authorized Gmail client service based on an auth code.
	* @param userEmail The Email address of the user.
	* @return An authorized Gmail client service
	* @throws java.io.IOException IOException occurs.
	*/
	public Gmail getGmailServiceFromRefreshToken(String userEmail) throws IOException {
		GmailPollingUser pollingUser = gmailRepository.findByUserId(userEmail);

		if (pollingUser == null) {
			System.out.println("No user found for email address: " + userEmail);
			return null;
		}

		String CLIENT_SECRET_FILE = "client_secret.json";
		String REDIRECT_URI = "https://unclutter.iminsys.com";

		// Exchange auth code for access token
		InputStream in = ItemListener.class.getResourceAsStream("/client_secret.json");
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
	* @param service The service that should be used to retrieve messages with.
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
