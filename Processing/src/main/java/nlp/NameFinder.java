package nlp;

import java.net.URL;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class NameFinder {
	public static final String fileName = "/names";

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