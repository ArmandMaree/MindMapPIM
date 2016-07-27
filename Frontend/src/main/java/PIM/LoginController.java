package hello;

import java.util.concurrent.*;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import java.lang.Thread;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.*;
import listeners.*;
import data.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController extends WebMvcConfigurerAdapter {
    private LinkedBlockingQueue<TopicResponse> responseLL = new LinkedBlockingQueue<TopicResponse>();

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/mainpage").setViewName("mainpage");
        registry.addViewController("/setting").setViewName("setting");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/help").setViewName("help");
    }
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ServerResponse accessTokenSend(UserRegistration message) throws Exception {
        System.out.println("Access Token: " + message);
        rabbitTemplate.convertAndSend("register.business.rabbit",message);
        Thread.sleep(3000);
        return new ServerResponse("200");
    }

    @MessageMapping("/request")
    @SendTo("/topic/request")
    public TopicResponse recieveRequest(TopicRequest request) throws Exception {
        System.out.println("Client is making a topic request");
        rabbitTemplate.convertAndSend("topic-request.business.rabbit",request);
        while(request.getUserId()==responseLL.peek().getUserId()){//wait for responseLL for new topics with user ID
            //do nothing for now, maybe sleep a bit in future?
        }

        return responseLL.poll();
    }
    
    public void receiveTopicResponse(TopicResponse topicResponse) {
        try{
            responseLL.put(topicResponse);
        }
        catch (InterruptedException ie){}
        // rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
    }


    @RequestMapping(value="/mainpage", method=RequestMethod.GET)
    public String showMain() {
        return "mainpage";
    }

    @RequestMapping(value="/settings", method=RequestMethod.GET)
    public String showSettings() {
        return "settings";
    }

    @RequestMapping(value="/help", method=RequestMethod.GET)
    public String showHelp() {
        return "help";
    }
}