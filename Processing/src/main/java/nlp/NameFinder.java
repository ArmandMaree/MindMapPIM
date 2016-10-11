package nlp;

import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
* Finds name from a wordlist of names.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class NameFinder {
	/**
	* Path to the resource (textfile) containing the names.
	*/
	public static final String fileName = "/names";

	/**
	* Determines if the provided string is a name by doing a look up in a word list.
	* @param name The word that should be looked up.
	* @return True if provided string is a name. False if it is not a name.
	*/
	public static boolean isName(String name) {
		String[] splitedName = name.split("\\s+");

		try {
			InputStream in = NameFinder.class.getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = reader.readLine()) != null) {
				for (String singleName : splitedName) {
					if (singleName.toUpperCase().equals(line)) {
						System.out.println("Matched " + name + " to " + line + ".");
						return true;
					}
				}
			}

			reader.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return false;
	}
}