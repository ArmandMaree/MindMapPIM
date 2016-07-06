package webservices.google;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class GoogleTokenController {
	@CrossOrigin
	@RequestMapping(value = "/google/token", method = RequestMethod.POST, headers = {"Content-type=application/json"})
	@ResponseBody    
	public String googleToken(@RequestBody AuthCode authCode) {
		System.out.println("CODE: " + authCode.getAuthCode());
		return "OK";
	}

	@RequestMapping("/check")    
	@ResponseBody
	public String check() {
		return "Check";
	}
}
