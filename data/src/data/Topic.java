package data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* A topic template for NoSQL repositories.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class Topic implements Serializable, Comparable<Topic> {
	/**
	* ID used in database.
	*/
	@Id
	private String id;

	/**
	* ID of the user in database the topic is related to.
	*/
	private String userId;

	/**
	* The topic's name.
	*/
	private String topic;

	/**
	* Array of topics that are related.
	*/
	private String[] relatedTopics;

	/**
	* IDs of the ProcessedData in the repository that contains this topic.
	*/
	private String[] processedDataIds;

	/**
	* Last time in milliseconds this topic was updated.
	*/
	private long time;

	public Topic() {
		super();
	}

	public Topic(String userId) {
		super();
		this.userId = userId;
	}

	/**
	* Default constructor.
	* @param userId ID of the user in database the topic is related to.
	* @param topic The topic's name.
	* @param relatedTopics Array of topics that are related.
	* @param processedDataIds IDs of the ProcessedData in the repository that contains this topic.
	* @param time Last time in milliseconds this topic was updated. Usually the current time.
	*/
	public Topic(String userId, String topic, String[] relatedTopics, String[] processedDataIds, long time) {
		this.userId = userId;
		this.topic = topic;
		this.relatedTopics = relatedTopics;
		this.processedDataIds = processedDataIds;
		this.time = time;
	}

	/**
	* Returns the value of id.
	* @return The ID used in the database.
	*/
	public String getId() {
		return id;
	}

	/**
	* Set the value of id.
	* @param id The ID used in the database.
	*/
	public void setId(String id) {
		this.id = id;
	}

	/**
	* Returns the value of userId.
	* @return ID of the user in database the topic is related to.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Set the value of userId.
	* @param id ID of the user in database the topic is related to.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns the value of topic.
	* @return The topic's name.
	*/
	public String getTopic() {
		return topic;
	}

	/**
	* Set the value of topic.
	* @param id The topic's name.
	*/
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	* Returns the value of relatedTopics.
	* @return Array of topics that are related.
	*/
	public String[] getRelatedTopics() {
		return relatedTopics;
	}

	/**
	* Set the value of relatedTopics.
	* @param id Array of topics that are related.
	*/
	public void setRelatedTopics(String[] relatedTopics) {
		this.relatedTopics = relatedTopics;
	}

	/**
	* Returns the value of processedDataIds.
	* @return IDs of the ProcessedData in the repository that contains this topic.
	*/
	public String[] getProcessedDataIds() {
		return processedDataIds;
	}

	/**
	* Set the value of processedDataIds.
	* @param id IDs of the ProcessedData in the repository that contains this topic.
	*/
	public void setProcessedDataIds(String[] processedDataIds) {
		this.processedDataIds = processedDataIds;
	}

	/**
	* Returns the value of time.
	* @return Last time in milliseconds this topic was updated.
	*/
	public long getTime() {
		return time;
	}

	/**
	* Set the value of time.
	* @param id Last time in milliseconds this topic was updated. Usually the current time.
	*/
	public void setTime(long time) {
		this.time = time;
	}

	/**
	* Returns a string representation of a topic used for printing.
	* @return Topic as a string.
	*/
	@Override
	public String toString() {
		String s = "Topic: {\n" +
			"\tid: " + id + "\n" +
			"\ttopic: " + topic + "\n" +
			"\tuserId: " + userId + "\n" +
			"\trelatedTopics: [\n";

			for (String relatedTopic : relatedTopics)
				s += "\t\t" + relatedTopic + "\n";

			s += "\t]\n" +
			"\tprocessedDataIds: [\n";

			for (String processedDataId : processedDataIds)
				s += "\t\t" + processedDataId + "\n";

			s += "\t]\n" +
			"\ttime: " + time + "\n" +
		"}";

		return s;
	}

	/*
	* Implements Comparable and allows topics to sorted by weight.
	* @param other The topic this one is compared to.
	*/
	public int compareTo(Topic other) {
		return -1 * (int)(getWeight() - other.getWeight());
	}

	/**
	* Calculated the weight of the topic at the current moment.
	*/
	public double getWeight() {
		long secs = (System.currentTimeMillis() - time) / 1000; // seconds between time and current time
		double hours = Math.max(secs / 3600, 0.01); // hours between time and current time with a minimum of 0.01
		double weight = (100 / hours) * processedDataIds.length; // inverse of hourse * 100 * number of data associated with topic.
		return weight;
	}
}
