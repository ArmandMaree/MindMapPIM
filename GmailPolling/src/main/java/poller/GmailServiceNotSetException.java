package poller;

/**
* Exception that is thrown if the poller is started but a Gmail service could not be started.
*
* @author Armand Maree
* @since  1.0.0
*/
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