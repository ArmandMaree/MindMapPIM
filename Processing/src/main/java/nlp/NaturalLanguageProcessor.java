package nlp;

import java.util.List;

/**
* Interface that defines what a language processor should look like.
*
* @author  Armand Maree
* @since   1.0.0
*/
public interface NaturalLanguageProcessor {
	/**
	* Extracts a list of topics from a given set of text.
	* @param text Contains the text that needs to be interpreted.
	* @return A list of topics discovered in the text.
	*/
	public List<String> getTopics(String text);

	/**
	* Removes all duplicates and all the words that also occur in the excludedWords list.
	* @param words List containg the words that need to be purged.
	* @return List of words that does not contain any of the excludedWords and no duplicates.
	*/
	public List<String> purge(List<String> words);

	/**
	* Takes a list of words and seperates it into names and non-names by using the {@link nlp.NameFinder} class.
	* @param words The list of words that should be checked.
	* @return A list of 2 elements. Index 0 is the topics and index 1 is the names.
	*/
	public List<List<String>> splitNamesAndTopics(List<String> words);
}
