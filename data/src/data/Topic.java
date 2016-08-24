package data;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;

/**
* A topic template for NoSQL repositories.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class Topic implements Serializable, Comparable<Topic> {
	private static final long serialVersionUID = 2698785136676361L;

	/**
	* The ID of the topic as used by the database.
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
	private List<String> relatedTopics;

	/**
	* IDs of the ProcessedData in the repository that contains this topic.
	*/
	private List<String> processedDataIds;

	/**
	* Last time in milliseconds this topic was updated.
	*/
	private long time;

	/**
	* Indicates whether this topic is a person.
	*/
	private boolean person = false;

	/**
	* Default constructor
	*/
	public Topic() {
		super();
	}

	/**
	* Constructor that initializes some member variables.
	* @param userId ID of the user in database the topic is related to.
	*/
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
	* @param time Last time in milliseconds this topic was updated.
	*/
	public Topic(String userId, String topic, List<String> relatedTopics, List<String> processedDataIds, long time) {
		this.userId = userId;
		this.topic = topic;
		addRelatedTopics(relatedTopics);
		this.processedDataIds = processedDataIds;
		this.time = time;
	}
	
	/**
	* Adds new topics to the already existing topics but does not duplicate topics and inserts them in alphabetical order.
	* @param newRelatedTopics The new topics that should be added.
	*/
	public void addRelatedTopics(List<String> newRelatedTopics) {
		if (relatedTopics == null)
			relatedTopics = new ArrayList<>();

		for (String relatedTopic : newRelatedTopics) {
			boolean found = false;
			int pos = 0;

			while (pos < relatedTopics.size() && relatedTopics.get(pos).compareTo(relatedTopic) >= 0) {
				if (relatedTopics.get(pos).equals(relatedTopic)) {
					found = true;
					break;
				}

				pos++;
			}

			if (!found)
				relatedTopics.add(pos, relatedTopic);
		}
	}

	/**
	* Adds new {@link data.ProcessedData} IDs to the already existing processedDataIds but does not duplicate IDs.
	* @param id The new id that should be added.
	*/
	public void addProcessedDataId(String id) {
		if (processedDataIds == null)
			processedDataIds = new ArrayList<>();
		
		if (!processedDataIds.contains(id))
			processedDataIds.add(id);
	}

	/**
	* Returns the value of id.
	* @return The ID of the topic as used by the database.
	*/
	public String getId() {
		return id;
	}

	/**
	* Set the value of id.
	* @param id The ID of the topic as used by the database.
	*/
	public void setId(String id) {
		this.id = id;
	}

	/**
	* Returns the value of userId.
	* @return ID of the topic in database the topic is related to.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Set the value of userId.
	* @param userId ID of the user in database the topic is related to.
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
	* @param topic The topic's name.
	*/
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	* Returns the value of relatedTopics.
	* @return Array of topics that are related.
	*/
	public List<String> getRelatedTopics() {
		return relatedTopics;
	}

	/**
	* Set the value of relatedTopics.
	* @param relatedTopics Array of topics that are related.
	*/
	public void setRelatedTopics(List<String> relatedTopics) {
		this.relatedTopics = relatedTopics;
	}

	/**
	* Returns the value of processedDataIds.
	* @return IDs of the ProcessedData in the repository that contains this topic.
	*/
	public List<String> getProcessedDataIds() {
		return processedDataIds;
	}

	/**
	* Set the value of processedDataIds.
	* @param processedDataIds IDs of the ProcessedData in the repository that contains this topic.
	*/
	public void setProcessedDataIds(List<String> processedDataIds) {
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
	* @param time Last time in milliseconds this topic was updated. Usually the current time.
	*/
	public void setTime(long time) {
		this.time = time;
	}

	/**
	* Returns the value of person.
	* @return Indicates whether this topic is a person.
	*/
	public boolean isPerson() {
		return person;
	}

	/**
	* Set the value of person.
	* @param person Indicates whether this topic is a person.
	*/
	public void setIsPerson(boolean person) {
		this.person = person;
	}

	/*
	* Implements Comparable and allows topics to sorted by weight.
	* @param other The topic this one is compared to.
	*/
	public int compareTo(Topic other) {
		if (getWeight() > other.getWeight())
			return 1;
		else if (getWeight() < other.getWeight())
			return -1;
		else 
			return 0;
	}

	/**
	* Calculated the weight of the topic at the current moment.
	* <p>
	*	Algorithm places a lot of weight on the temperal difference between when the topic was last modified and now, but it also takes into concideration the amount of times this topic came up.<br>
	*	Algorithm:<br>
	*	weight = 100 * (number of hours between now and 01/01/2005) * (number of times topic came up)
	* </p>
	*
	* @return The weight of this topic in terms of a temperal component and a frequency component.
	*/
	public double getWeight() {
		long secs = time / 1000; // seconds between time and current time
		double hours = secs / 3600 - 306816; // hours since 01/01/2005
		double weight = 100 * hours * processedDataIds.size(); // inverse of hourse * 100 * number of data associated with topic.
		return weight;
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
				s += "\t\t" + processedDataId + ",\n";

			s += "\t]\n" +
			"\ttime: " + time + ",\n" +
			"\tisPerson: " + person + "\n" +
		"}";

		return s;
	}
}
