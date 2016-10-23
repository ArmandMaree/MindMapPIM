package listeners;

/**
* Exception that is thrown when a user is requested that doesn't exist.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class NoSuchUserException extends Exception {
	public NoSuchUserException() {
		super();
	}

	public NoSuchUserException(String message) {
		super(message);
	}

	public NoSuchUserException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchUserException(Throwable cause) {
		super(cause);
	}

}