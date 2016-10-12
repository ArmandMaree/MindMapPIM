package poller;

/**
* Exception that gets throws when a poller is already running for a specific user.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class AlreadyPollingForUserException extends Exception {
	public AlreadyPollingForUserException() {
		super();
	}

	public AlreadyPollingForUserException(String message) {
		super(message);
	}

	public AlreadyPollingForUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyPollingForUserException(Throwable cause) {
		super(cause);
	}
}