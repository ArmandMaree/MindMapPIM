package nlp;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.dcoref.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.*;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.*;

/**
* Implements a NaturalLanguageProcessor for the Stanford CoreNLP API.
*
* @see <a href="http://nlp.stanford.edu">Stanford NLP</a>
* @author  Armand Maree
* @since   2016-07-11
*/
public class StanfordNLP implements NaturalLanguageProcessor {
	private PrintWriter out = null;
	private PrintWriter xmlOut = null;
	private StanfordCoreNLP pipeline = null;
	private ArrayList<String> excludedWords = new ArrayList<>();

	/**
	* Initializes some fields and sets the appropriot Properties for the Stanford NLP API.
	* @param out The output device (usually System.out) to which the status and working should be printed to. Use null if no status is needed.
	* @param xmlOut The output device (usually a file) to which an XML version of the output can be written to. Use null if no XML output is needed.
	*/
	public StanfordNLP (PrintWriter out, PrintWriter xmlOut) {
	    this.out = out;
		this.xmlOut = xmlOut;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
		init(props);
	}

	/**
	* Initializes some fields and sets the appropriot Properties for the Stanford NLP API. Output devices are set to null.
	*/
	public StanfordNLP() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
		init(props);
	}

	/**
	* Initializes the StanfordCoreNLP pipeline.
	* @param props The Properties containg primarily what should be searched for during parsing.
	*/
	private void init(Properties props) {
		pipeline = new StanfordCoreNLP(props);

		excludedWords.add("dear");
		excludedWords.add("kind");
		excludedWords.add("fwd");
		excludedWords.add("regards");
		excludedWords.add("hi");
		excludedWords.add("hello");
		excludedWords.add("re");
		excludedWords.add("lot");
		excludedWords.add("unsubscribe");
		excludedWords.add("time");
		excludedWords.add("email");
	}

	/**
	* Extracts a list of topics from a given set of text.
	* @param text Contains the text that needs to be interpreted.
	* @return A list of topics discovered in the text.
	*/
	public ArrayList<String> getTopics(String text) {
		List<CoreMap> sentences = parse(text);
		// ArrayList<String> topics = getPOS("VB", sentences); // get verbs
		// topics.addAll(getGroups(sentences)); // get nouns grouped by NamedEntityTagAnnotation
		ArrayList<String> topics = getGroups(sentences); // get nouns grouped by NamedEntityTagAnnotation

		return topics;
	}

	/**
	* Converts the text into a list of words along with their metadata.
	* @param text The text that should be parsed. A single well written sentence works best.
	* @return A list of words with their metadata.
	*/
	private List<CoreMap> parse(String text) {
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// this prints out the results of sentence analysis to file(s) in good formats
		if (out != null)
			pipeline.prettyPrint(document, out);

	    if (xmlOut != null) {
			try {
				pipeline.xmlPrint(document, xmlOut);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(1);
			}
	    }

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		return sentences;
	}

	/**
	* Gets all the words whos part of speech matches a certain specified prefix/part of speech.
	* @param match The prefix/word that will be used to match the parts of speech of the words. Use * as the last character of the prefix to indicate it as a prefix.
	* @param sentences List of words with their metadata.
	* @return A list of words at their base form that match the given criteria.
	*/
	private ArrayList<String> getPOS(String match, List<CoreMap> sentences) {
		ArrayList<String> words = new ArrayList<>();

		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String pos = token.get(PartOfSpeechAnnotation.class);

				if (match.endsWith("*") && pos.startsWith(match.substring(0, match.length() - 1)))
					words.add(token.get(LemmaAnnotation.class));
				else if (pos.equals(match))
					words.add(token.get(LemmaAnnotation.class));
			}
		}

		return words;
	}

	/**
	* Gets all the words whos part of speech matches a certain specified prefix/part of speech.
	* @param match The prefix/word that will be used to match the parts of speech of the words. Use * as the last character of the prefix to indicate it as a prefix.
	* @param sentences List of words with their metadata.
	* @return A list of words as they were given form that match the given criteria.
	*/
	private ArrayList<CoreLabel> getCore(String match, List<CoreMap> sentences) {
		ArrayList<CoreLabel> words = new ArrayList<>();

		for(CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String pos = token.get(PartOfSpeechAnnotation.class);

				if (match.endsWith("*") && pos.startsWith(match.substring(0, match.length() - 1)))
					words.add(token);
				else if (pos.equals(match))
					words.add(token);
			}
		}

		return words;
	}

	/**
	* Gets all the nouns from a given set and groups words whos NamedEntityTag matches.
	* @param sentences List of words with their metadata.
	* @return A list of words at their base form that match the given criteria.
	*/
	private ArrayList<String> getGroups(List<CoreMap> sentences) {
		ArrayList<CoreLabel> preGroup = getCore("NN*", sentences);
		ArrayList<String> groups = new ArrayList<>();
		ArrayList<CoreLabel> buffer = new ArrayList<>();

		for (CoreLabel token: preGroup) {
			String ner = token.get(NamedEntityTagAnnotation.class);

			if (ner.equals("O")) {
				if (!buffer.isEmpty())
					groups.add(bufferToString(buffer));

				buffer = new ArrayList<>();
				groups.add(token.get(LemmaAnnotation.class));
			}
			else {
				if (!ner.equals("PERSON") && !ner.equals("LOCATION") && !ner.equals("ORGANIZATION")) {
					if (!buffer.isEmpty())
						groups.add(bufferToString(buffer));

					buffer = new ArrayList<>();
				}
				else if (buffer.isEmpty())
					buffer.add(token);
				else if (ner.equals(buffer.get(0).get(NamedEntityTagAnnotation.class)))
					buffer.add(token);
				else {
					groups.add(bufferToString(buffer));
					buffer = new ArrayList<>();
					buffer.add(token);
				}
			}
		}

		if (!buffer.isEmpty())
			groups.add(bufferToString(buffer));

		return groups;
	}

	/**
	* Converts a list of metadata words to a string of words seperated by a space.
	* @param buffer The list of words and their metadata.
	* @return A single String of words seperated by a space.
	*/
	private String bufferToString(ArrayList<CoreLabel> buffer) {
		String bufferGroup = "";

		if (!buffer.isEmpty()) {
			for (CoreLabel word : buffer) {
				if (bufferGroup.equals(""))
					bufferGroup = word.get(LemmaAnnotation.class);
				else
					bufferGroup += " " + word.get(LemmaAnnotation.class);
			}
		}

		return bufferGroup;
	}

	/**
	* Removes all duplicates and all the words that also occur in the excludedWords list.
	* @param words List containg the words that need to be purged.
	* @return List of words that does not contain any of the excludedWords and no duplicates.
	*/
	public ArrayList<String> purge(List<String> words) {
		ArrayList<String> remainingWords = new ArrayList<>();
		Pattern p = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

		for (String word : words) {
			Matcher m = p.matcher(word);

			if (!excludedWords.contains(word.toLowerCase()) && !remainingWords.contains(word) && word.length() > 1 && !m.matches())
				remainingWords.add(word);
		}

		return remainingWords;
	}
}
