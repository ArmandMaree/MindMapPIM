package main;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import poller.*;
import processor.*;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

		ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
		
		ArrayList<Poller> pollers = new ArrayList<>();
		pollers.add(new GmailPoller(queue, ""));
		new Thread(pollers.get(0)).start();

		ArrayList<DataProcessingThread> processorThreads = new ArrayList<>();
		processorThreads.add(new DataProcessingThread(queue));

		new Thread(processorThreads.get(0)).start();
	}

}