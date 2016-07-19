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


@Controller
public class LoginController extends WebMvcConfigurerAdapter {

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
        try{
            URL url = new URL("http://localhost:50001/google/token");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            byte[] out = message.getAuthCode().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            System.out.println(http.getInputStream());
        }catch(Exception e){

        }
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