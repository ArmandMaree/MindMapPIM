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
	private LinkedBlockingQueue queue;
	private String userId;

	public GmailPoller(LinkedBlockingQueue queue, String userId) {
		this.queue = queue;
		this.userId = userId;
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
}
