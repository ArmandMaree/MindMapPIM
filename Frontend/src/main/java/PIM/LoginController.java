package hello;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Controller
public class LoginController extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/mainpage").setViewName("mainpage");
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showLogin(Login loginForm) {
        return "login";
    }

    @RequestMapping(value="/mainpage", method=RequestMethod.GET)
    public String showMain() {
        return "mainpage";
    }
}