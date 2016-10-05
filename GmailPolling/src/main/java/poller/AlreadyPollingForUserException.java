package poller;

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