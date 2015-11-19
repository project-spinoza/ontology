package org.projectspinoza.ontology.util;


public class Term {
//	private static Logger log = LogManager.getRootLogger();
	
	private String title;
	private String term;
	private String tags;
	private String description;
	private int frequency = 1;
	private int overAllFrequency = 1;
	
	public Term(){}
	public Term(String term){
		this.term = term;
	}
	public Term(String term, String title, String description, String tags){
		this.term = term;
		this.title = title;
		this.description = description;
		this.tags = tags;
	}
	public String getTags(){
		return this.tags;
	}
	public void setTags(String tags){
		this.tags = tags;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFrequency() {
		return frequency;
	}
	public void incrementFrequency(){
		frequency++;
	}
	public void setOverAllFrequency(int overAllFrequency) {
		this.overAllFrequency = overAllFrequency;
	}
	public int getOverAllFrequency() {
		return overAllFrequency;
	}
	public void incrementoverAllFrequency(){
		overAllFrequency++;
	}
	@Override
	public String toString(){
		return "term["+term+"], title["+title+"], description[...], frequency["+frequency+"], overallFrequency["+overAllFrequency+"]";
	}
}
