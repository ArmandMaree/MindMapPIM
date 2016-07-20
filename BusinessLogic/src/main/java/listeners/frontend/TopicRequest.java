package listeners.frontend;

import java.io.Serializable;

/**
* A request for new topics based on a given path.
*
* @author  Armand Maree
* @since   2016-07-20
*/
public class TopicRequest implements Serializable {
	private String[] path = {"path1", "path2"};

	/**
	* Create string representation of TopicRequest for printing
	* @return
	*/
	@Override
	public String toString() {
		String p = "";

		for (String item : path) {
			if (p.equals(""))
				p += item;
			else {
				p += "-" + item;
			}
		}

		return "TopicRequest{\n" +
			"\tpath=" + p + "\n" +
			"}";
	}
}
