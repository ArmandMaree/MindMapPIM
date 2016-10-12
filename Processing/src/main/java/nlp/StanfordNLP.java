package nlp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
* Implements a NaturalLanguageProcessor for the Stanford CoreNLP API.
*
* @see <a href="http://nlp.stanford.edu">Stanford NLP</a>
* @author  Armand Maree
* @since   1.0.0
*/
public class StanfordNLP implements NaturalLanguageProcessor {
	/**
	* The output device (usually System.out) to which the status and working should be printed to. Use null if no status is needed.
	*/
	private PrintWriter out = null;

	/**
	* The output device (usually a file) to which an XML version of the output can be written to. Use null if no XML output is needed.
	*/
	private PrintWriter xmlOut = null;

	/**
	*
	*/
	private StanfordCoreNLP pipeline = null;

	/**
	* A list of words that should always be excluded.
	*/
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

		try {
			FileInputStream fis = new FileInputStream("/home/armand/gradle/exclude/armand.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String line;

			while ((line = reader.readLine()) != null)
				if (!line.equals("") && !excludedWords.contains(line))
					excludedWords.add(line);

			reader.close();

			fis = new FileInputStream("/home/armand/gradle/exclude/arno.txt");
			reader = new BufferedReader(new InputStreamReader(fis));

			while ((line = reader.readLine()) != null)
				if (!line.equals("") && !excludedWords.contains(line))
					excludedWords.add(line);

			reader.close();

			fis = new FileInputStream("/home/armand/gradle/exclude/amy.txt");
			reader = new BufferedReader(new InputStreamReader(fis));

			while ((line = reader.readLine()) != null)
				if (!line.equals("") && !excludedWords.contains(line))
					excludedWords.add(line);

			reader.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println(excludedWords.size() + " excluded words.");
	}

	/**
	* Extracts a list of topics from a given set of text.
	* @param text Contains the text that needs to be interpreted.
	* @return A list of topics discovered in the text.
	*/
	public List<String> getTopics(String text) {
		try {
			List<CoreMap> sentences = parse(text);
			// ArrayList<String> topics = getPOS("VB", sentences); // get verbs
			// topics.addAll(getGroups(sentences)); // get nouns grouped by NamedEntityTagAnnotation
			List<String> topics = getGroups(sentences); // get nouns grouped by NamedEntityTagAnnotation

			return topics;
		}
		catch(OutOfMemoryError oome) {
			return null;
		}
	}

	/**
	* Converts the text into a list of words along with their metadata.
	* @param text The text that should be parsed. A single well written sentence works best.
	* @return A list of words with their metadata.
	*/
	private List<CoreMap> parse(String text) throws OutOfMemoryError {
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
	private List<String> getPOS(String match, List<CoreMap> sentences) {
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
	private List<CoreLabel> getCore(String match, List<CoreMap> sentences) {
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
	private List<String> getGroups(List<CoreMap> sentences) {
		List<CoreLabel> preGroup = getCore("NN*", sentences);
		List<String> groups = new ArrayList<>();
		List<CoreLabel> buffer = new ArrayList<>();

		for (CoreLabel token: preGroup) {
			String ner = token.get(NamedEntityTagAnnotation.class);

			if (ner.equals("O")) {
				if (!buffer.isEmpty())
					groups.add(bufferToString(buffer));

				buffer = new ArrayList<>();
				String word = token.get(LemmaAnnotation.class);

				if (!excludedWords.contains(word))
					groups.add(word);
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
					String word = bufferToString(buffer);

					if (!excludedWords.contains(word))
						groups.add(word);

					buffer = new ArrayList<>();
					buffer.add(token);
				}
			}
		}

		if (!buffer.isEmpty()) {
			String word = bufferToString(buffer);

			if (!excludedWords.contains(word))
				groups.add(word);
		}

		return groups;
	}

	/**
	* Converts a list of metadata words to a string of words seperated by a space.
	* @param buffer The list of words and their metadata.
	* @return A single String of words seperated by a space.
	*/
	private String bufferToString(List<CoreLabel> buffer) {
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
	public List<String> purge(List<String> words) {
		List<String> remainingWords = new ArrayList<>();
		Pattern p = Pattern.compile("^.*[^a-zA-Z0-9\\s]+.*$");

		for (String word : words) {
			Matcher m = p.matcher(word);

			if (word.length() > 1 && !m.matches() && !excludedWords.contains(word.toLowerCase())) {
				boolean found = false;

				for (String topic : remainingWords) {
					if (topic.toLowerCase().equals(word.toLowerCase())) {
						found = true;
						break;
					}
				}

				if (!found)
					remainingWords.add(word);
			}
		}

		return remainingWords;
	}
}
