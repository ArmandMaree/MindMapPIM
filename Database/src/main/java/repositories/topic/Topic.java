package repositories.topic;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class Topic implements Serializable {
	@Id
	private String id;

	private String userId;
	private String topic;
	private String[] relatedTopics;
	private String[] processedDataIds;
	private long time;

	public Topic(String userId, String topic, String[] relatedTopics, String[] processedDataIds, long time) {
		this.userId = userId;
		this.topic = topic;
		this.relatedTopics = relatedTopics;
		this.processedDataIds = processedDataIds;
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String[] getRelatedTopics() {
		return relatedTopics;
	}

	public void setRelatedTopics(String[] relatedTopics) {
		this.relatedTopics = relatedTopics;
	}

	public String[] getProcessedDataIds() {
		return processedDataIds;
	}

	public void setProcessedDataIds(String[] processedDataIds) {
		this.processedDataIds = processedDataIds;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		String s = "Topic: {\n" +
			"\tid: " + id + "\n" +
			"\tuserId: " + userId + "\n" +
			"\trelatedTopics: [\n";

			for (String relatedTopic : relatedTopics)
				s += "\t\t" + relatedTopic + "\n";

			s += "\t]\n" +
			"\tprocessedDataIds: [\n";

			for (String processedDataId : processedDataIds)
				s += "\t\t" + processedDataId + "\n";

			s += "\t]\n" +
		"}";

		return s;
	}
}
