package PIM;

import java.util.UUID;
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
import data.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController extends WebMvcConfigurerAdapter {
    @Autowired
    @Qualifier("topicResponseLL")
    LinkedBlockingQueue<TopicResponse> topicResponseLL;

    @Autowired
    @Qualifier("itemResponseLL")
    LinkedBlockingQueue<ItemResponseIdentified> itemResponseLL;

    @Autowired
    @Qualifier("userResponseLL")
    LinkedBlockingQueue<UserIdentified> userRegistrationResponseLL;

    @Autowired
    @Qualifier("userCheckResponseLL")
    LinkedBlockingQueue<UserIdentified> userCheckResponseLL;
//////////////////
    @Autowired
    @Qualifier("editUserSettingsResponseLL")
    LinkedBlockingQueue<EditUserSettingsResponse> editUserSettingsResponseLL;

/////////////////////
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/mainpage").setViewName("mainpage");
        registry.addViewController("/setting").setViewName("setting");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("mainpage");
        registry.addViewController("/help").setViewName("help");
    }
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showLogin() {
        return "mainpage";
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public ServerResponse accessTokenSend(UserRegistration message) throws Exception {
		String id = UUID.randomUUID().toString();
        if(message.getAuthCodes()[0].getAuthCode()!=null){
    		UserRegistrationIdentified userRegistrationIdentified = new UserRegistrationIdentified(id, message);
            System.out.println(userRegistrationIdentified);
            rabbitTemplate.convertAndSend("register.business.rabbit",userRegistrationIdentified);
            while(userRegistrationResponseLL.peek()==null || !id.equals(userRegistrationResponseLL.peek().getReturnId())){
				//do nothing for now, maybe sleep a bit in future?
            }
    		User user = userRegistrationResponseLL.poll().getUser(true);
            System.out.println(user);
            Thread.sleep(2000);
            return new ServerResponse(user.getUserId());
        }else{
            System.out.println(message);
            UserIdentified userIdentified = new UserIdentified(id,false, message.getFirstName(),message.getLastName(),message.getAuthCodes()[0].getId());
            rabbitTemplate.convertAndSend("user-check.database.rabbit",userIdentified);
            while(userCheckResponseLL.peek()==null || !id.equals(userCheckResponseLL.peek().getReturnId())){
                //do nothing for now, maybe sleep a bit in future?
            }
            UserIdentified user = userCheckResponseLL.poll();
            System.out.println(user);

            Thread.sleep(2000);
            return new ServerResponse(user.getIsRegistered());
        }
    }
////////////// AMY

    @MessageMapping("/usercheck")
    @SendTo("/topic/request")
    public UserIdentified usercheck(User message) throws Exception {
        System.out.println(message);
        String id = UUID.randomUUID().toString();
        System.out.println("Request id: "+id);
        UserIdentified userRegistrationIdentified = new UserIdentified(id,false, message);
        rabbitTemplate.convertAndSend("user-check.database.rabbit",userRegistrationIdentified);
        while(userCheckResponseLL.peek()==null || !id.equals(userCheckResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        System.out.println("Found user!");
        UserIdentified user = userCheckResponseLL.poll();
        System.out.println("Found user: "+user);

        Thread.sleep(2000);
        return user;
    }

    @MessageMapping("/datasources")
    @SendTo("/topic/request")
    public EditUserSettingsResponse sendNewDataSources(UserRegistrationIdentified message) throws Exception {
        String id = UUID.randomUUID().toString();
        message.setId(id);
        System.out.println(message);
        rabbitTemplate.convertAndSend("register.business.rabbit",message);
        while(editUserSettingsResponseLL.peek()==null || !id.equals(editUserSettingsResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        EditUserSettingsResponse response = editUserSettingsResponseLL.poll();
        System.out.println(response);
        Thread.sleep(2000);
        return response;
    }

    // @MessageMapping("/theme")
    // @SendTo("/settings/request")
    // public EditSettingsResponse editTheme (EdiThemeRequest message) throws Exception {
    //     System.out.println(message);
    //     String id = UUID.randomUUID().toString();
    //     message.setReturnId(id);

    //     rabbitTemplate.convertAndSend("settings.database.rabbit",message);
    //     while(editSourcesResponseLL.peek()==null || !id.equals(editSourcesResponseLL.peek().getReturnId())){
    //         //do nothing for now, maybe sleep a bit in future?
    //     }
    //     System.out.println("Received response!");
    //     EditSettingsResponse response = editSourcesResponseLL.poll();
    //     System.out.println("Settings response: " +resonse);

    //     Thread.sleep(2000);
    //     return response;
    // }

/////////////////
    public ServerResponse userchecktest(User message) throws Exception {
        String id = "123456";
        UserIdentified userRegistrationIdentified = new UserIdentified(id,false, message);
        rabbitTemplate.convertAndSend("user-check.database.rabbit",userRegistrationIdentified);
        while(userCheckResponseLL.peek()==null || !id.equals(userCheckResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        UserIdentified user = userCheckResponseLL.poll();
        // Thread.sleep(2000);
        return new ServerResponse(user.getIsRegistered());
    }

    @MessageMapping("/request")
    @SendTo("/topic/request")
    public TopicResponse recieveRequest(TopicRequest request) throws Exception {
        System.out.println(request);
        if(!request.getUserId().contains("mocktesting")){
            rabbitTemplate.convertAndSend("topic-request.business.rabbit",request);
            while(topicResponseLL.peek()==null || !request.getUserId().equals(topicResponseLL.peek().getUserId())){//wait for responseLL for new topics with user ID
                //do nothing for now, maybe sleep a bit in future?
            }

    		TopicResponse topicResponse = topicResponseLL.poll();
            System.out.println(topicResponse);
            // Thread.sleep(2000);
            return topicResponse;
        }else{
            String [][][] mockpimIds =  new String[4][2][2];
            mockpimIds[0][0][0] = "1";
            mockpimIds[0][0][1] = "2";
            mockpimIds[1][0][0] = "3";
            mockpimIds[1][0][1] = "4";
            mockpimIds[2][0][0] = "5";
            mockpimIds[2][0][1] = "6";
            mockpimIds[3][0][0] = "7";
            mockpimIds[3][0][1] = "8";

            TopicResponse topicResponse = new TopicResponse(request.getUserId(),new String[]{"Hello","Its","Me","Nobody"},mockpimIds);
            System.out.println(topicResponse);
            // thread.sleep(2000);
            return topicResponse;
        }
    }

    @MessageMapping("/gmailItems")
    @SendTo("/topic/request")
    public ItemResponse recieveItemRequest(GmailItemRequest request) throws Exception {
        if(!request.getUserId().contains("mocktesting")){
            String id = UUID.randomUUID().toString();
            ItemRequestIdentified  itemRequestIdentified = new ItemRequestIdentified(id,request.getItemIds(),request.getUserId());
            System.out.println(itemRequestIdentified);
            rabbitTemplate.convertAndSend("item-request.gmail.rabbit",itemRequestIdentified);
            while(itemResponseLL.peek()==null || !id.equals(itemResponseLL.peek().getReturnId())){//wait for itemResponseLL for new topics with user ID
                //do nothing for now, maybe sleep a bit in future?
            }

            ItemResponseIdentified itemResponse = itemResponseLL.poll();
            System.out.println(itemResponse);
            ItemResponse ir = new ItemResponse(itemResponse.getItems());
            System.out.println(ir);
            // Thread.sleep(2000);
            return ir;
        }else{
            System.out.println(request);
            String [] itemresponse = new String[2];
            for(int i=0;i<request.getItemIds().length;i++){
                switch (request.getItemIds()[i]) {
                    case "1": itemresponse[i] = "Hey Acuban\n How is the weather?";break;
                    case "2": itemresponse[i] = "Reminder about school fees";break;
                    case "3": itemresponse[i] = "Horse riding club just lost all the horses";break;
                    case "4": itemresponse[i] = "Hey Acuban\n When are you available for coffee";break;
                    case "5": itemresponse[i] = "Thank you so much for my horse sized mug and the horse!";break;
                    case "6": itemresponse[i] = "Hmmmm";break;
                    case "7": itemresponse[i] = "Random email\n Random random random";break;
                    case "8": itemresponse[i] = "Hey Acuban\n How is father?";break;
                }
            }
            ItemResponse ir = new ItemResponse(itemresponse);
            System.out.println(ir);
            // Thread.sleep(2000);
            return ir;
        }
    }

    public void receiveTopicResponse(TopicResponse topicResponse) {
        try {
            topicResponseLL.put(topicResponse);
        }
        catch (InterruptedException ie){}
        // rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
    }

    public void receiveItemResponse(ItemResponseIdentified itemResponse) {
        try {
            itemResponseLL.put(itemResponse);
        }
        catch (InterruptedException ie){}
        // rabbitTemplate.convertAndSend(topicResponseQueueName, topicResponse);
    }

	public void receiveUserRegistrationResponse(UserIdentified user) {
		try {
            userRegistrationResponseLL.put(user);
        }
        catch (InterruptedException ie){}
	}

    public void receiveUserCheckResponse(UserIdentified user) {
        try {
            userCheckResponseLL.put(user);
        }
        catch (InterruptedException ie){}
    }
///////////////
    //  public void receiveEditSettingsResponse(EditUserSettingsResponse response) {
    //     try {
    //         editUserSettingsResponseLL.put(response);
    //     }
    //     catch (InterruptedException ie){}
    // }

///////////////////
    @RequestMapping(value="/login", method=RequestMethod.GET)
    public String showMain() {
        return "login";
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
