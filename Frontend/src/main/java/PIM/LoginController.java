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
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;

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
    LinkedBlockingQueue<UserUpdateResponseIdentified> editUserSettingsResponseLL;

/////////////////////
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

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
    		User user = userRegistrationResponseLL.poll();
            System.out.println(user);
            // Thread.sleep(2000);
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

            // Thread.sleep(2000);
            return new ServerResponse(user.getIsRegistered(),user.getUserId());
        }
    }
////////////// AMY

    @MessageMapping("/usercheck")
    @SendToUser("/topic/request")
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

        return user;
    }

    @MessageMapping("/datasources")
    @SendToUser("/topic/request")
    public UserUpdateResponseIdentified sendNewDataSources(UpdateSources message) throws Exception {
        String id = UUID.randomUUID().toString();
        if(message.getUserId() == null)
            System.out.println("No userId");
        else
            System.out.println("User id: "+message.getUserId());
        if(message.getAuthcodes() == null)
            System.out.println("No authcodes");

        UserUpdateRequestIdentified request = new UserUpdateRequestIdentified(id,message.getUserId(),message.getAuthcodes());
        //System.out.println("Java auth codes length:"+ message.getAuthcodes().length);
        request.setTheme(null);
        request.setInitialDepth(-1);
        request.setBranchingFactor(-1);
        System.out.println(request);
        rabbitTemplate.convertAndSend("user-update-request.business.rabbit",request);

        while(editUserSettingsResponseLL.peek()==null || !id.equals(editUserSettingsResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        UserUpdateResponseIdentified response = editUserSettingsResponseLL.poll();
        System.out.println("Update data sourcesresponse:" +response);
        return response;
    }

    @MessageMapping("/theme")
    // @SendTo("/topic/request")
    @SendToUser("/topic/request")
    public UserUpdateResponseIdentified editTheme(Theme message) throws Exception {
        String id = UUID.randomUUID().toString();
        UserUpdateRequestIdentified request = new UserUpdateRequestIdentified(id,message.getUserId(),null);
        request.setTheme(message.getTheme());
        request.setInitialDepth(-1);
        request.setBranchingFactor(-1);
        System.out.println(request);
 
        rabbitTemplate.convertAndSend("user-update-request.business.rabbit",request);
        while(editUserSettingsResponseLL.peek()==null || !id.equals(editUserSettingsResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        System.out.println("Received response!");
        UserUpdateResponseIdentified response = editUserSettingsResponseLL.poll();
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,0);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,99);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,1);
        System.out.println("Settings response: " +response.getCode());

        return response;
    }
    @MessageMapping("/mapsettings")
    // @SendTo("/topic/request")
    @SendToUser("/topic/request")
    public UserUpdateResponseIdentified editTheme (MapSettings message) throws Exception {
        String id = UUID.randomUUID().toString();
        UserUpdateRequestIdentified request = new UserUpdateRequestIdentified(id,message.getUserId(),null);
        request.setInitialDepth(message.getInitialDepth());
        request.setBranchingFactor(message.getInitialBranchFactor());
        
        System.out.println(request);
        
        rabbitTemplate.convertAndSend("user-update-request.business.rabbit",request);
       System.out.println("After send");
        while(editUserSettingsResponseLL.peek()==null || !id.equals(editUserSettingsResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        System.out.println("Received request with id: " +id);
        UserUpdateResponseIdentified response = editUserSettingsResponseLL.poll();
        //erUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,0);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,99);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,1);
        System.out.println("Settings response: " +response);

        return response;
    }

    @MessageMapping("/deactivate")
    // @SendTo("/topic/request")
    @SendToUser("/topic/request")
    public UserUpdateResponseIdentified deactivateAccount (Deactivate message) throws Exception {
        String id = UUID.randomUUID().toString();
        UserUpdateRequestIdentified request = new UserUpdateRequestIdentified(id,message.getUserId(),null);
        request.setInitialDepth(-1);
        request.setBranchingFactor(-1);
        request.setIsActive(message.getIsActive());
        
        System.out.println(request);
        
        rabbitTemplate.convertAndSend("user-update-request.business.rabbit",request);
       System.out.println("After send");
        while(editUserSettingsResponseLL.peek()==null || !id.equals(editUserSettingsResponseLL.peek().getReturnId())){
            //do nothing for now, maybe sleep a bit in future?
        }
        System.out.println("Received request with id: " +id);
        UserUpdateResponseIdentified response = editUserSettingsResponseLL.poll();
        //erUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,0);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,99);
        //UserUpdateResponseIdentified response = new UserUpdateResponseIdentified(id,1);
        System.out.println("Settings response: " +response);

        return response;
    }

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
    @SendToUser("/topic/request")
    public TopicResponse recieveRequest(TopicRequest request) throws Exception {
        System.out.println(request);
        // if(!request.getUserId().contains("mocktesting")){
            rabbitTemplate.convertAndSend("topic-request.business.rabbit",request);
            while(topicResponseLL.peek()==null || !request.getUserId().equals(topicResponseLL.peek().getUserId())){//wait for responseLL for new topics with user ID
                //do nothing for now, maybe sleep a bit in future?
            }
 
    		TopicResponse topicResponse = topicResponseLL.poll();
            System.out.println(topicResponse);
            // Thread.sleep(2000);
            // this.simpMessagingTemplate.convertAndSend("/queue/chats-" + request.getUserId(), topicResponse);
            return topicResponse;
        }
        else{
            String [][][] mockpimIds =  new String[4][2][2];
            mockpimIds[0][0][0] = "1";
            mockpimIds[0][0][1] = "2";
            mockpimIds[1][0][0] = "3";
            mockpimIds[1][0][1] = "4";
            mockpimIds[2][0][0] = "5";
            mockpimIds[2][0][1] = "6";
            mockpimIds[3][0][0] = "7";
            mockpimIds[3][0][1] = "8";
            // TopicResponse topicResponse = new TopicResponse(request.getUserId(),new String[]{"Hello","Its"},new String[]{"Arno Grobler", "Amy Lochner","Armand Maree","Tyrone Waston"},mockpimIds);
            TopicResponse topicResponse = new TopicResponse(request.getUserId(),new String[]{"Horse","cos301","photo","recipe"},new String[]{"Horse","cos301","photo","recipe"},mockpimIds);
            System.out.println(topicResponse);
            // this.simpMessagingTemplate.convertAndSend("/user/topic/request", topicResponse);
            // thread.sleep(2000);
            return topicResponse;
        }
    }

    @MessageMapping("/gmailItems")
    @SendToUser("/topic/request")
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
     public void receiveEditSettingsResponse(UserUpdateResponseIdentified response) {
        try {
            editUserSettingsResponseLL.put(response);
        }
        catch (InterruptedException ie){}
    }

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
