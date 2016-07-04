package data;

public class ProcessedData {
	public String pimSource = "";
	public String userId = "";
	public String involvedContacts = "";
	public String[] topics = null;
	public String pimItemId = "";

	public ProcessedData(RawData rawData, String[] topics) {
		this.pimSource = rawData.pimSource;
		this.userId = rawData.userId;
		this.involvedContacts = rawData.involvedContacts;
		this.pimItemId = rawData.pimItemId;
		this.topics = topics;
	}
}