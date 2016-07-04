package data;

public class ProcessedData {
	public String pimSource = "";
	public String userId = "";
	public String[] involvedContacts = null;
	public String[] topics = null;
	public String pimItemId = "";

	public ProcessedData(RawData rawData, String[] topics) {
		this.pimSource = rawData.pimSource;
		this.userId = rawData.userId;
		this.involvedContacts = rawData.involvedContacts;
		this.pimItemId = rawData.pimItemId;
		this.topics = topics;
	}

	@Override
	public String toString() {
		String s = "ProcessedData: {\n" +
			"\tpimSource: " + pimSource + "\n" +
			"\tuserId: " + userId + "\n" +
			"\tinvolvedContacts: [\n";

		if (involvedContacts != null)
			for (String contact : involvedContacts) {
				s += "\t\t" + contact + "\n";
			}
		else
			s += "\t\tnone\n";

		s += "\t]\n" +
			"\tpimItemId: " + pimItemId + "\n" +
			"\ttopics: [\n";

		if (topics != null)
			for (String topic : topics) {
				s += "\t\t" + topic + "\n";
			}
		else
			s += "\t\tnone\n";

		s += "\t]\n}";

		return s;
	}
}