package org.projectspinoza.ontology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class Main {
	private static Logger log = LogManager.getRootLogger();

	public static void main(String[] args) {
		if (args.length != 2) {
			log.error("Please provide two arguments, e.g tweets.txt ontologies.json");
			System.exit(0);
		}
		log.info("Initializing ontologies!");
		(new TermOntologyMatcher("tweets.txt", "ontologies.json")).matchTerms();
		log.info("Done!");
	}
}
