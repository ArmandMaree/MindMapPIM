# Unclutter (Mindmap PIM)

Travis Status: <a href="https://travis-ci.com/ArmandMaree/MindMapPIM" target="_blank"><img src="https://travis-ci.com/ArmandMaree/MindMapPIM.svg?token=EDzLz4Cfsmpc3FQfSzFR&branch=master" alt="Travis Status Image"></a>

Unclutter is a personal information manager. It collects information about you from various other sources (like your Gmail and Facebook account) and finds relationships between events, people and objects that you frequently come in contact with. This collected information is displayed back to you in the form of an interactive mind map that allows you to explore recent events and the people that shared these events with you.

## Framework and Technologies

* Unclutter is written in Java and uses the Spring Boot framework.
* Gradle is used as the build tool.
* RabbitMQ is used to commpunicate between services.

## Architecture and Design

Unclutter uses a microservices architecture with pipes and filters aspects. It consists of 4 primary services (frontend, business, database, and processor) and then a dynamic module service (called a polling service) for each platform.

### Frontend Service

The frontend service's primary function is handling connections to the server through Spring MVC. It also makes requests to other backend services based on certain requests make from the client devices.

### Business Service

The business service puts certain tasks in motion based on various requests it receives. For instance, it starts and stops required pollers during user registration based on which platforms the user selected.

### Database Service

The database services is used to persist data. MongoDB is used as the DBMS.

### Processor Service

This service has the responsibility of receiving a piece of text and then responding with a set of extracted topics. The topics are words or phrases that describe what the given text is about. These topics are in turn the content that is displayed in each bubble on the frontend client.

### Polling Service

The system may consist of many polling services running at once. A polling service is an program that retrieves information from a certain platform based on certain requests it receives. For instance, if a uses registers to unclutter and signs in with their Gmail account, then the Gmail Polling Service will start retrieving all the required information of the user from Gmail. Each of these services run completely independantly from the rest of the system since there is NO coupling between them and the other services.

## Other features

Unclutter uses an API to implement the polling services. Thus any external developers that would like to develop a poller for a specific platform, can simply use the JAR file that is located in the Poller directory. The developed poller will be reviewed and deployed as a module on Unclutter.
