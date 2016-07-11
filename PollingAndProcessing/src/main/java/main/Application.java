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
	public RawDataQueue rawQueue() {
		return new RawDataQueue();
	}

	@Bean
	public ProcessedDataQueue ProcessedQueue() {
		return new ProcessedDataQueue();
	}

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		RawDataQueue rawQueue = (RawDataQueue)ctx.getBean(RawDataQueue.class);
		ProcessedDataQueue processedQueue = (ProcessedDataQueue)ctx.getBean(ProcessedDataQueue.class);
		// NaturalLanguageProcessor nlp = new StanfordNLP();
		NaturalLanguageProcessor nlp = null;
		DataProcessingThread[] dataProcessingThreads = new DataProcessingThread[10];

		for (DataProcessingThread dpt : dataProcessingThreads) {
			dpt = new DataProcessingThread(rawQueue, processedQueue, nlp);
			new Thread(dpt).start();
		}

		System.out.println("Started " + dataProcessingThreads.length + " data processing threads.");
	}
}
