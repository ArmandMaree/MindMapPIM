package com.unclutter.poller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.AmqpException;

/**
* A simplified interface to interact with the backend. This class should be used by the pollers and should be instantiated by the MessageBrokerFactory.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class MessageBroker {
	private RabbitTemplate rabbitTemplate;

	/**
	* Constructor that initializes some variables.
	* @param rabbitTemplate Used to send messages via RabbitMQ.
	*/
	public MessageBroker(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	/**
	* Send a raw data object to a non priority queue for processing.
	* @param rawData The object that contains all the information of the item that needs to be processed.
	*/
	public void sendRawData(RawData rawData) throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("raw-data.processing.rabbit", rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe.getMessage(), ampqe);
		}
	}

	/**
	* Send a raw data object to a priority queue for processing.
	* @param rawData The object that contains all the information of the item that needs to be processed.
	*/
	public void sendPriorityRawData(RawData rawData) throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("priority-raw-data.processing.rabbit", rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe);
		}
	}

	/**
	* Return an item that was requested to be retrieved.
	* @param itemResponse The object that contains the item.
	*/
	public void sendItem(ItemResponseIdentified itemResponse) throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("item-response.frontend.rabbit", itemResponse);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe);
		}
	}
}
