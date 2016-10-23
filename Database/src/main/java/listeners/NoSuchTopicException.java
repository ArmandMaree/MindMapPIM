package listeners;

/**
* Exception that is thrown when a topic is requested that doesn't exist.
*
* @author  Armand Maree
* @since   1.0.0
*/
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