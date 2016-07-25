package nlp;

import java.util.List;
import java.util.ArrayList;

/**
* Interface that defines what a language processor should look like.
*
* @author  Armand Maree
* @since   2016-07-11
*/
public interface NaturalLanguageProcessor {
	/**
	* Extracts a list of topics from a given set of text.
	* @param text Contains the text that needs to be interpreted.
	* @return A list of topics discovered in the text.
	*/
	public ArrayList<String> getTopics(String text);

	/**
	* Removes all duplicates and all the words that also occur in the excludedWords list.
	* @param words List containg the words that need to be purged.
	* @return List of words that does not contain any of the excludedWords and no duplicates.
	*/
	public ArrayList<String> purge(List<String> words);
}
