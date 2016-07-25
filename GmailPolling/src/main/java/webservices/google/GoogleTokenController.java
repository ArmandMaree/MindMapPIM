package webservices.google;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.*;

import org.springframework.stereotype.Component;

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
	@CrossOrigin
	@RequestMapping(value = "/gmail/token", method = RequestMethod.POST, headers = {"Content-type=application/json"})
	@ResponseBody
	public String googleToken(@RequestBody AuthCode authCode) {
		Poller poller = new GmailPoller(authCode.getAuthCode());
		new Thread(poller).start();
		return "OK";
	}
}
