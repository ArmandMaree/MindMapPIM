package poller;

public class GmailServiceNotSetException extends Exception {
	public GmailServiceNotSetException() {
		super();
	}

	public GmailServiceNotSetException(String message) {
		super(message);
	}

	public GmailServiceNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public GmailServiceNotSetException(Throwable cause) {
		super(cause);
	}
}