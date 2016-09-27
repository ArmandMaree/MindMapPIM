package com.unclutter.poller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class MessageBroker {
	private RabbitTemplate rabbitTemplate;

	public MessageBroker(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendRawData(RawData rawData) {
		rabbitTemplate.convertAndSend("raw-data.processing.rabbit", rawData);
	}

	public void sendPriorityRawData(RawData rawData) {
		rabbitTemplate.convertAndSend("priority-raw-data.processing.rabbit", rawData);
	}

	public void sendItem(ItemResponseIdentified itemResponse) {
		rabbitTemplate.convertAndSend("item-response.frontend.rabbit", itemResponse);
	}
}
