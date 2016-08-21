package listeners;

import data.*;

import java.util.List;

public class PendingTopic {
	private String topic;
	private ProcessedData processedData;
	private List<String> remainingTopics;

	public PendingTopic(String topic, ProcessedData processedData, List<String> remainingTopics) {
		this.topic = topic;
		this.processedData = processedData;
		this.remainingTopics = remainingTopics;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setProcessedData(ProcessedData processedData) {
		this.processedData = processedData;
	}

	public ProcessedData getProcessedData() {
		return processedData;
	} 

	public void setRemainingTopics(List<String> remainingTopics) {
		this.remainingTopics = remainingTopics;
	}

	public List<String> getRemainingTopics() {
		return remainingTopics;
	}
}