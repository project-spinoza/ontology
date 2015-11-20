package org.projectspinoza.ontology;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.projectspinoza.ontology.util.DataLoader;
import org.projectspinoza.ontology.util.MatchedTerm;
import org.projectspinoza.ontology.util.Term;

public class TermOntologyMatcher {
	private static Logger log = LogManager.getRootLogger();

	private String tweetsPath;
	private String ontologiesPath;
	private List<MatchedTerm> matches;
	public List<String> not_matched;

	public TermOntologyMatcher() {
	}

	public TermOntologyMatcher(String tweetsPath, String ontologiesPath) {
		this.tweetsPath = tweetsPath;
		this.ontologiesPath = ontologiesPath;
		not_matched = new ArrayList<String>();
	}

	public List<MatchedTerm> matchTerms() {
		int matchedCount = 0;
		List<String> tweetTags = DataLoader.fetchTags(tweetsPath);
		List<Map<String, String>> ontologies = DataLoader
				.fetchOntologies(ontologiesPath);
		matches = new ArrayList<MatchedTerm>();
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
		printTestMatched(matchedCount);
		return null;
	}

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

	public List<MatchedTerm> getMatches() {
		return matches;
	}

	public void setMatches(List<MatchedTerm> matches) {
		this.matches = matches;
	}

	public void addToRelation(String term, Map<String, String> ontology) {
		for (int i = 0; i < matches.size(); i++) {
			if (matches.get(i).getParent().getTerm()
					.equals(ontology.get("Parent").trim())) {
				matches.get(i).addChild(
						new Term(term, ontology.get("Title"), ontology.get(
								"Body").replaceAll("[\r\n]+", ""), ontology
								.get("Tag")));
				return;
			}
		}
		MatchedTerm relation = new MatchedTerm(new Term(ontology.get("Parent")
				.trim()));
		relation.addChild(new Term(term, ontology.get("Title"), ontology.get(
				"Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
		matches.add(relation);
	}

	public void printTestMatched(int matchedCount) {
		String results = "results.txt";
//		log.debug("matched [" + matchedCount + "]");
		FileWriter fw = null;
		StringBuilder sb = new StringBuilder();
		try {
			fw = new FileWriter(new File(results));
			sb.append("{\"matched\":[");
			for (int i = 0; i < matches.size(); i++) {
				MatchedTerm relation = matches.get(i);
				sb.append("{\"parent\":\"" + relation.getParent().getTerm()
						+ "\"");
				sb.append(",\"childs\":[");
				int numChilds = relation.getChilds().size();
				for (int j = 0; j < numChilds; j++) {
					Term child = relation.getChilds().get(j);
					sb.append("{");
					sb.append("\"term\":\"" + child.getTerm() + "\",");
					sb.append("\"title\":\"" + child.getTitle() + "\",");
//					int desLength = child.getDescription().length() < 80 ? child
//							.getDescription().length() : 32;

					sb.append("\"description\":\"...\",");
					sb.append("\"tags\":\"" + child.getTags() + "\",");
					sb.append("\"frequency\":\"" + child.getFrequency() + "\",");
					sb.append("\"overAllFrequency\":\""
							+ child.getOverAllFrequency() + "\"");
					sb.append("}");
					if (j < (numChilds - 1)) {
						sb.append(",");
					}
				}
				sb.append("]}");
				if (i < (matches.size() - 1)) {
					sb.append(",");
				}
			}
			sb.append("],\"not_matched\":[");
			int unmatched_size = not_matched.size();
			for (int i = 0; i < unmatched_size; i++) {
				String comma = ",";
				if (i == unmatched_size - 1) {
					comma = "";
				}
				sb.append("\"" + not_matched.get(i) + "\"" + comma);
			}
			sb.append("]}");
			fw.write(sb.toString());
			fw.close();
			log.debug(matchedCount+" matched Tags");
			log.info(unmatched_size+" Un Matched Tags");
//			int total = matchedCount + unmatched_size;
//			log.info(total+" total Tags");
//		//	double percentage = 0.0;
//			if (matchedCount != 0) {
//				double percentage = (matchedCount / (matchedCount + unmatched_size)) * 100;
//				log.debug(percentage+"% tags matched");
//			}
			
			log.info("Results are stored in [" + results + "]");
		} catch (IOException ex) {

		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {

			}
		}

	}
}
