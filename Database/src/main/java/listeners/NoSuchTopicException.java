package listeners;

public class NoSuchTopicException extends Exception {
	public NoSuchTopicException() {
		super();
	}

	public NoSuchTopicException(String message) {
		super(message);
	}

	public NoSuchTopicException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchTopicException(Throwable cause) {
		super(cause);
	}

}