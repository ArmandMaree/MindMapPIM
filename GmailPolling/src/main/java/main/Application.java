package main;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.PrintWriter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import poller.*;
import data.*;

/**
* Main application that starts up the service.
*
* @author  Armand Maree
* @since   2016-07-11
*/
@SpringBootApplication
@ComponentScan({"webservices"})
public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}
}
