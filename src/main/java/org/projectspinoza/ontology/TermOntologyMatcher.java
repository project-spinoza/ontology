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
import org.projectspinoza.ontology.util.Relation;
import org.projectspinoza.ontology.util.Term;

public class TermOntologyMatcher {
	private static Logger log = LogManager.getRootLogger();
	
	private String tweetsPath;
	private String ontologiesPath;
	private List<Relation> matches;
	
	public TermOntologyMatcher(){}
	public TermOntologyMatcher(String tweetsPath, String ontologiesPath){
		this.tweetsPath = tweetsPath;
		this.ontologiesPath = ontologiesPath;
	}
	public List<Relation> matchTerms(){	
		List<String> tweetTags = DataLoader.fetchTags(tweetsPath);
		List<Map<String, String>> ontologies = DataLoader.fetchOntologies(ontologiesPath);

		matches = new ArrayList<Relation>();
		for(Map<String, String> ontology : ontologies){
			String ontoTagsString = ontology.get("Tag").toLowerCase();
			String[] ontoTags = ontoTagsString.split(" ");
			for(String ontoTag : ontoTags){
				ontoTag = ontoTag.replaceAll(",", "").trim();
				for(String tag : tweetTags){
					if(ontoTag.equals(tag)){
						addToRelation(tag, ontology);
					}
				}
			}
		}
		printTestMatched();
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
	public List<Relation> getMatches() {
		return matches;
	}
	public void setMatches(List<Relation> matches) {
		this.matches = matches;
	}
	public void addToRelation(String term, Map<String, String> ontology){
		for(int i = 0; i < matches.size(); i++){
			if(matches.get(i).getParent().getTerm().equals(ontology.get("Parent").trim())){
				matches.get(i).addChild(new Term(term, ontology.get("Title"), ontology.get("Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
				return;
			}
		}
		Relation relation = new Relation(new Term(ontology.get("Parent").trim()));
		relation.addChild(new Term(term, ontology.get("Title"), ontology.get("Body").replaceAll("[\r\n]+", ""), ontology.get("Tag")));
		matches.add(relation);
	}
	public void printTestMatched(){
		String results = "results.txt";
		log.debug("matched ["+matches.size()+"]");
		FileWriter fw = null;
		StringBuilder sb = new StringBuilder();
		try{
			fw = new FileWriter(new File(results));
			sb.append("{\"results\":[");
			for(int i=0; i<matches.size(); i++){
				Relation relation = matches.get(i);
				sb.append("{\"parent\":\""+relation.getParent().getTerm()+"\"");
				sb.append(",\"childs\":[");
				int numChilds = relation.getChilds().size();
				for(int j=0; j<numChilds; j++){
					Term child = relation.getChilds().get(j);
					sb.append("{");
					sb.append("\"term\":\""+child.getTerm()+"\",");
					sb.append("\"title\":\""+child.getTitle()+"\",");
					//sb.append("\"description\":\""+child.getDescription().replaceAll("\"", "")+"\",");
					sb.append("\"description\":\"...\",");
					sb.append("\"tags\":\""+child.getTags()+"\"");
					sb.append("}");
					if( j < (numChilds-1)){
						sb.append(",");
					}
				}
				sb.append("]}");
				if(i < (matches.size()-1)){
					sb.append(",");
				}
			}
			sb.append("]}");
			fw.write(sb.toString());
			fw.close();
			log.info("Results are stored in ["+results+"]");
		}catch(IOException ex){
			
		}finally{
			try{
				if(fw != null){
					fw.close();
				}
			}catch(IOException ex){
				
			}
		}
	}
}
