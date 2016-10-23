package com.unclutter.poller;

/**
* Exception that is thrown when a message could not be sent on RabbitMQ.
*
*/
public class MessageNotSentException extends Exception {
	public MessageNotSentException() {
		super();
	}

	public MessageNotSentException(String message) {
		super(message);
	}

	public MessageNotSentException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageNotSentException(Throwable cause) {
		super(cause);
	}

}