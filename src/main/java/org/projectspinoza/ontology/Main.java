package org.projectspinoza.ontology;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class Main {
	private static Logger log = LogManager.getRootLogger();
    public static void main( String[] args )
    {
        log.info( "Initializing ontologies!" );
        (new TermOntologyMatcher("tweetsData.txt", "ontologiesData.json")).matchTerms();
        log.info( "Done!" );
    }
}
