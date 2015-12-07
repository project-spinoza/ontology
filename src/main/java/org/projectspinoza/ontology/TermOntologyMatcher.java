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
    private List<Term> matched;
    public List<String> unmatched;

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
        return matched;
    }

    public void setMatches(List<Term> matches) {
        this.matched = matches;
    }

    public TermOntologyMatcher() {
    }

    public TermOntologyMatcher(String tweetsPath, String ontologiesPath) {
        this.tweetsPath = tweetsPath;
        this.ontologiesPath = ontologiesPath;
        unmatched = new ArrayList<String>();
    }

    public List<Term> matchTerms() {
        int matchedCount = 0;
        List<String> tweetTags = DataLoader.fetchTags(tweetsPath);
        List<Map<String, String>> ontologies = DataLoader
                .fetchOntologies(ontologiesPath);
        matched = new ArrayList<Term>();
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

            for (int i = 0; i < matched.size(); i++) {
                for (int j = 0; j < matched.get(i).getChilds().size(); j++) {
                    if (matched.get(i).getChilds().get(j).getTerm().equals(tag)) {
                        matched.get(i).getChilds().get(j)
                                .setOverAllFrequency(rootCounter);
                    }
                }
            }
            if (rootCounter == 0) {
                unmatched.add(tag);
            } else {
                matchedCount++;
            }
        }
        makeHierarchy();
        printTestMatched(matchedCount, unmatched.size());
        return null;
    }

    public void addToRelation(String term, Map<String, String> ontology) {
        for (int i = 0; i < matched.size(); i++) {
            if (matched.get(i).getTerm().equals(ontology.get("Parent").trim())) {
                matched.get(i).addChild(
                        new Term(term, ontology.get("Title"), ontology.get(
                                "Body").replaceAll("[\r\n]+", ""), ontology
                                .get("Tag")));
                return;
            }
        }
        Term relation = new Term(ontology.get("Parent").trim());
        relation.addChild(new Term(term, ontology.get("Title"), ontology.get(
                "Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
        matched.add(relation);
    }

    public void makeHierarchy() {
        int finalMatches = matched.size();
        for (int i = 0; i < finalMatches; i++) {
            Term relation = matched.get(i);
            String parent = relation.getTerm().toLowerCase();
            for (int k = 0; k < finalMatches; k++) {
                if (k != i) {
                    Term relation2 = matched.get(k);
                    for (int j = 0; j < relation2.getChilds().size(); j++) {
                        if (parent.equals(relation2.getChilds().get(j)
                                .getTerm().toLowerCase())) {
                            relation2.getChilds().get(j)
                                    .setChilds(relation.getChilds());
                            matched.remove(relation);
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
            mapper.writeValue(fw, matched);
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
