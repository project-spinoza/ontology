package org.projectspinoza.ontology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static Logger log = LogManager.getRootLogger();

    public static void main(String[] args) {
        if (args.length != 2) {
            log.error("Please provide two arguments, e.g tweets.txt ontologies.json");
            System.exit(0);
        }
        log.info("Initializing ontologies!");
        (new TermOntologyMatcher(args[0], args[1])).start();
        log.info("Done!");
    }
}
