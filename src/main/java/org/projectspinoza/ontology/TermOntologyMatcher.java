package org.projectspinoza.ontology;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.projectspinoza.ontology.util.DataLoader;
import org.projectspinoza.ontology.util.Term;

public class TermOntologyMatcher {
	private static Logger log = LogManager.getRootLogger();

	private String tweetsPath;
	private String ontologiesPath;
	private List<Term> matches;
	public List<String> not_matched;
	
	public String getTweetsPath() {
		return tweetsPath;
	}
	public void setTweetsPath(String tweetsPath) {
		this.tweetsPath = tweetsPath;
	}

	public String getOntologiesPath() {
		return ontologiesPath;
	}
	public void setOntologiesPath(String ontologiesPath) {
		this.ontologiesPath = ontologiesPath;
	}

	public List<Term> getMatches() {
		return matches;
	}
	public void setMatches(List<Term> matches) {
		this.matches = matches;
	}
	
	public TermOntologyMatcher() {
	}
	public TermOntologyMatcher(String tweetsPath, String ontologiesPath) {
		this.tweetsPath = tweetsPath;
		this.ontologiesPath = ontologiesPath;
		not_matched = new ArrayList<String>();
	}

	public List<Term> matchTerms() {
		int matchedCount = 0;
		List<String> tweetTags = DataLoader.fetchTags(tweetsPath);
		List<Map<String, String>> ontologies = DataLoader
				.fetchOntologies(ontologiesPath);
		matches = new ArrayList<Term>();
		for (String tag : tweetTags) {
			int rootCounter = 0;
			for (Map<String, String> ontology : ontologies) {
				String ontoTagsString = ontology.get("Tag").toLowerCase();
				String[] ontoTags = ontoTagsString.replaceAll(" ", "").split(
						",");
				for (String ontoTag : ontoTags) {
					if (ontoTag.equals(tag)) {
						rootCounter++;
						addToRelation(tag, ontology);
					}
				}
			}
			log.debug(tag + ", matched " + rootCounter + " times");

			for (int i = 0; i < matches.size(); i++) {
				for (int j = 0; j < matches.get(i).getChilds().size(); j++) {
					if (matches.get(i).getChilds().get(j).getTerm().equals(tag)) {
						matches.get(i).getChilds().get(j)
								.setOverAllFrequency(rootCounter);
					}
				}
			}
			if (rootCounter == 0) {
				not_matched.add(tag);
			} else {
				matchedCount++;
			}
		}
		makeHierarchy();
		printTestMatched(matchedCount, not_matched.size());
		return null;
	}

	public void addToRelation(String term, Map<String, String> ontology) {
		for (int i = 0; i < matches.size(); i++) {
			if (matches.get(i).getTerm().equals(ontology.get("Parent").trim())) {
				matches.get(i).addChild(
						new Term(term, ontology.get("Title"), ontology.get(
								"Body").replaceAll("[\r\n]+", ""), ontology
								.get("Tag")));
				return;
			}
		}
		Term relation = new Term(ontology.get("Parent").trim());
		relation.addChild(new Term(term, ontology.get("Title"), ontology.get(
				"Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
		matches.add(relation);
	}

	public void makeHierarchy() {
		int finalMatches = matches.size();
		for (int i = 0; i < finalMatches; i++) {
			Term relation = matches.get(i);
			String parent = relation.getTerm().toLowerCase();
			for (int k = 0; k < finalMatches; k++) {
				if (k != i) {
					Term relation2 = matches.get(k);
					for (int j = 0; j < relation2.getChilds().size(); j++) {
						if (parent.equals(relation2.getChilds().get(j).getTerm()
										.toLowerCase())) {
							relation2.getChilds().get(j)
									.setChilds(relation.getChilds());
							matches.remove(relation);
							finalMatches--;
						}
					}
				}
			}
		}
	}

	public void printTestMatched(int matchedCount, int unmatchedCount) {
		String results = "results.txt";
		log.info("matched [" + matchedCount + "]");
		FileWriter fw = null;

		try {
			fw = new FileWriter(new File(results));
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(fw, matches);
			fw.close();

			float total = matchedCount + unmatchedCount;
			float percentage = 0.0F;
			if (total != 0) {
				percentage = ((matchedCount * 100.0F) / total);
				log.info(String.format("%.2f", percentage) + "% tags matched");
			}
			log.info("Results are stored in [" + results + "]");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
