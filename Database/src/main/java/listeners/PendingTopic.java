package listeners;

import data.*;

import java.util.List;

/**
* This class contains all the information of a specific topic with regard to a specific {@link data.ProcessedData} item. 
*
* <p>
*	This class serves basically as an intermediate class between a {@link data.ProcessedData} object and a {@link data.Topic} object. This allows for faster processing of {@link data.ProcessedData} before insertion into the database, i.e. all heavy processing can happen concurrently on seperate {@link data.ProcessedData} items while a single thread adds PendingTopics to the database.
* </p>
*
* @author  Armand Maree
* @since   1.0.0
*/
public class PendingTopic {
	/**
	* The topic's name.
	*/
	private String topic;

	/**
	* The {@link data.ProcessedData} object that this specific topic comes from.
	*/
	private ProcessedData processedData;

	/**
	* The topics contained in the {@link data.ProcessedData} member variable, but it excludes the topic contained in the topic member variable.
	*/
	private List<String> remainingTopics;

	/**
	* Indicates whether this topic is a contact or not.
	*/
	private boolean person = false;

	/**
	* @param topic The name of the topic.
	* @param processedData The {@link data.ProcessedData} object that this specific topic comes from.
	* @param remainingTopics The topics contained in the {@link data.ProcessedData} member variable, but it excludes the topic contained in the topic member variable.
	*/
	public PendingTopic(String topic, ProcessedData processedData, List<String> remainingTopics) {
		this.topic = topic;
		this.processedData = processedData;
		this.remainingTopics = remainingTopics;
	}

	/**
	* Sets the value of topic.
	* @param topic The name of the topic.
	*/
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	* Returns the value of topic.
	* @return The name of the topic.
	*/
	public String getTopic() {
		return topic;
	}

	/**
	* Sets the value of processedData.
	* @param processedData The {@link data.ProcessedData} object that this specific topic comes from.
	*/
	public void setProcessedData(ProcessedData processedData) {
		this.processedData = processedData;
	}

	/**
	* Returns the value of processedData
	* @return The {@link data.ProcessedData} object that this specific topic comes from.
	*/
	public ProcessedData getProcessedData() {
		return processedData;
	} 

	/**
	* Sets the value of remainingTopics.
	* @param remainingTopics The topics contained in the {@link data.ProcessedData} member variable, but it excludes the topic contained in the topic member variable.
	*/
	public void setRemainingTopics(List<String> remainingTopics) {
		this.remainingTopics = remainingTopics;
	}

	/**
	* Returns the value of remainingTopics.
	* @return The topics contained in the {@link data.ProcessedData} member variable, but it excludes the topic contained in the topic member variable.
	*/
	public List<String> getRemainingTopics() {
		return remainingTopics;
	}

	/**
	* Returns the value of person.
	* @return Indicates whether this topic is a contact.
	*/
	public boolean isPerson() {
		return person;
	}

	/**
	* Set the value of person.
	* @param person Indicates whether this topic is a contact.
	*/
	public void setIsPerson(boolean person) {
		this.person = person;
	}
}