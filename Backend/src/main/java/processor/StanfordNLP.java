package processor;

import java.io.*;
import java.util.*;

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

public class StanfordNLP implements NaturalLanguageProcessor {
	private PrintWriter out = null;
	private PrintWriter xmlOut = null;
	private StanfordCoreNLP pipeline = null;

	public StanfordNLP (PrintWriter out, PrintWriter xmlOut) {
	    this.out = out;
		this.xmlOut = xmlOut;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
		init(props);
	}

	public StanfordNLP() {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions");
		init(props);
	}

	private void init(Properties props) {
		pipeline = new StanfordCoreNLP(props);
	}

	public ArrayList<String> getTopics(String text) {
		List<CoreMap> sentences = parse(text);
		ArrayList<String> topics = getPOS("VB", sentences); // get verbs
		topics.addAll(getPOS("NN*", sentences)); // get nouns

		return topics;
	}

	public List<CoreMap> parse(String text) {
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
}
