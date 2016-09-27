package com.unclutter.poller;

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