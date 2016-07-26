package hello;

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
import org.springframework.beans.factory.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


@Controller
public class LoginController extends WebMvcConfigurerAdapter {
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
    public void accessTokenSend(AccessToken message) throws Exception {
        System.out.println("Access Token: " + message.getAuthCode());
        String userID = "";
        String[] path= null;
        String[] exclude = null;
        int maxNumberOfTopics= 4;
        TopicRequest topicrequest = new TopicRequest(userID,path,exclude,maxNumberOfTopics);
        rabbitTemplate.convertAndSend("register.business.rabbit",message);
        System.out.println("Rabbit templete sent to business rabbit:\n"+topicrequest);
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