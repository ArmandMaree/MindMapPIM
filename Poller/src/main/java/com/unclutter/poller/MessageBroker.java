package com.unclutter.poller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.AmqpException;

public class MessageBroker {
	private RabbitTemplate rabbitTemplate;

	public MessageBroker(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendRawData(RawData rawData) throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("raw-data.processing.rabbit", rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe.getMessage(), ampqe);
		}
	}

	public void sendPriorityRawData(RawData rawData)throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("priority-raw-data.processing.rabbit", rawData);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe);
		}
	}

	public void sendItem(ItemResponseIdentified itemResponse)throws MessageNotSentException {
		try {
			rabbitTemplate.convertAndSend("item-response.frontend.rabbit", itemResponse);
		}
		catch (AmqpException ampqe) {
			System.out.println("Could not send message to RabbitMQ.");
			throw new MessageNotSentException(ampqe);
		}
	}
}
