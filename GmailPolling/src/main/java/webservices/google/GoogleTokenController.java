package webservices.google;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Component;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import data.*;
import poller.*;

/**
* RESTController waiting for an Google authentication code on /google/token
*
* @author  Armand Maree
* @since   2016-07-11
*/
@RestController
@Configurable
public class GoogleTokenController {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@CrossOrigin
	@RequestMapping(value = "/google/token", method = RequestMethod.POST, headers = {"Content-type=application/json"})
	@ResponseBody
	public String googleToken(@RequestBody AuthCode authCode) {
		System.out.println("AuthCode: " + authCode.getAuthCode() + "   " + rabbitTemplate);
		Poller poller = new GmailPoller(rabbitTemplate, authCode.getAuthCode());
		new Thread(poller).start();
		return "OK";
	}
}
