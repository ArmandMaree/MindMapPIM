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
import processor.*;
import data.*;

@SpringBootApplication
@ComponentScan("webservices")
public class Application {
	@Bean
	public RawDataQueue queue() {
		return new RawDataQueue();
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		RawDataQueue queue = (RawDataQueue)ctx.getBean(RawDataQueue.class);
		NaturalLanguageProcessor nlp = new StanfordNLP();
		DataProcessingThread[] dataProcessingThreads = new DataProcessingThread[10];

		for (DataProcessingThread dpt : dataProcessingThreads) {
			dpt = new DataProcessingThread(queue, nlp);
		}
	}
}
