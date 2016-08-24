package nlp;

import java.net.URL;
import java.io.BufferedReader;
import java.io.FileReader;

public class NameFinder {
	public static final String fileName = "/names";

	public static boolean isName(String name) {
		URL fileURL = NameFinder.class.getResource(fileName);
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileURL.getPath()))) {
			for(String line; (line = br.readLine()) != null; ) {
				if (name.toUpperCase().contains(line))
					return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}