package org.projectspinoza.ontology;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public TermOntologyMatcher() {
    }

    public TermOntologyMatcher(String tweetsFilePath, String ontologiesFilePath) {
        this.tweetsPath = tweetsFilePath;
        this.ontologiesPath = ontologiesFilePath;
    }

    @SuppressWarnings("unchecked")
    public void start() {
        Map<String, Object> resultTerms = matchTerms(tweetsPath, ontologiesPath);
        List<Term> earlyMatchedTerms = (List<Term>) resultTerms.get("matched");
        List<String> unMatchedTerms = (List<String>) resultTerms.get("unMatched");
        List<Term> hieraricalTerms = finalHierarchy(earlyMatchedTerms);
        printTestMatched(hieraricalTerms, unMatchedTerms);
    }

    public Map<String, Object> matchTerms(String tweeetsfile,String ontologiesfile) {

        List<Term> matched = new ArrayList<Term>();
        List<String> unMatched = new ArrayList<String>();
        Map<String, Object> matchTermResult = new HashMap<String, Object>();
        List<String> tweetTags = DataLoader.fetchTags(tweeetsfile);
        List<Map<String, String>> ontologies = DataLoader.fetchOntologies(ontologiesfile);
        
        for (String tag : tweetTags) {
            int rootCounter = 0;
            for (Map<String, String> ontology : ontologies) {
                String ontoTagsString = ontology.get("Tag").toLowerCase();
                String[] ontoTags = ontoTagsString.replaceAll(" ", "").split(
                        ",");
                for (String ontoTag : ontoTags) {
                    if (ontoTag.equals(tag)) {
                        rootCounter++;
                        addToRelation(tag, ontology, matched);
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
                unMatched.add(tag);
            }

        }
        matchTermResult.put("matched", matched);
        matchTermResult.put("unMatched", unMatched);

        return matchTermResult;
    }

    public List<Term> addToRelation(String term, Map<String, String> ontology,
            List<Term> matchedTerms) {
        for (int i = 0; i < matchedTerms.size(); i++) {
            if (matchedTerms.get(i).getTerm().equals(ontology.get("Parent").trim())) {
                matchedTerms.get(i).addChild( new Term(term, ontology.get("Title"), ontology.get(
                                "Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
                return matchedTerms;
            }
        }
        Term relation = new Term(ontology.get("Parent").trim());
        relation.addChild(new Term(term, ontology.get("Title"), ontology.get(
                "Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
        matchedTerms.add(relation);
        
        return matchedTerms;
    }

    public List<Term> finalHierarchy(List<Term> matchedTerms) {
        int matchedTermsSize = matchedTerms.size();
        for (int i = 0; i < matchedTermsSize; i++) {
            Term relation = matchedTerms.get(i);
            String parent = relation.getTerm().toLowerCase();
            for (int k = 0; k < matchedTermsSize; k++) {
                if (k != i) {
                    Term relation2 = matchedTerms.get(k);
                    for (int j = 0; j < relation2.getChilds().size(); j++) {
                        if (parent.equals(relation2.getChilds().get(j).getTerm().toLowerCase())) {
                            relation2.getChilds().get(j).setChilds(relation.getChilds());
                            matchedTerms.remove(relation);
                            matchedTermsSize--;
                        }
                    }
                }
            }
        }
        return matchedTerms;
    }

    public void printTestMatched(List<Term> matchTerms,List<String> unMatchTerms) {

        int matchedCount = matchTerms.size();
        int unMatchedCount = unMatchTerms.size();
        log.info("matched [" + matchedCount + "]");
        FileWriter fw = null;

        try {
            fw = new FileWriter(new File("results.txt"));
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(fw, matchTerms);
            fw.close();

            float total = matchedCount + unMatchedCount;
            float percentage = 0.0F;
            if (total != 0) {
                percentage = ((matchedCount * 100.0F) / total);
                log.info(String.format("%.2f", percentage) + "% tags matched");
            }
            log.info("Results are stored in result file");
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
