package data;

public class RawData {
	public String pimSource = "";
	public String userId = "";
	public String[] involvedContacts = null;
	public String[] data = null;
	public String pimItemId = "";

	@Override
	public String toString() {
		String s = "RawData: {\n" +
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
			"\tdataCount: " + ((data == null) ? 0 : data.length) + "\n" +
			"}";

		return s;
	}
}