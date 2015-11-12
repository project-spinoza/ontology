package org.projectspinoza.ontology.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Term {
	private static Logger log = LogManager.getRootLogger();
	
	private String title;
	private String term;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}

	private String description;
	
	public Term(){}
	public Term(String term){
		this.term = term;
	}
	public Term(String term, String title, String description){
		this.term = term;
		this.title = title;
		this.description = description;
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
	
	@Override
	public String toString(){
		return "term["+term+"], title["+title+"], description["+description+"]";
	}
}
