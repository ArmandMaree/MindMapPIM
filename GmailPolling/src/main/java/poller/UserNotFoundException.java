package poller;

/**
* Exception that is thrown if a poller is started for a user that does not exist.
*
* @author Armand Maree
* @since 1.0.0
*/
public class UserNotFoundException extends Exception {
	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}
}