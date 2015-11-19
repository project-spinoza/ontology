package org.projectspinoza.ontology.util;

import java.util.ArrayList;
import java.util.List;

public class MatchedTerm {

	private Term parent;
	private List<Term> childs;

	public MatchedTerm() {
	}

	public MatchedTerm(Term parent) {
		this.parent = parent;
	}

	public Term getParent() {
		return parent;
	}

	public void setParent(Term parent) {
		this.parent = parent;
	}

	public List<Term> getChilds() {
		return childs;
	}

	public void setChilds(List<Term> childs) {
		this.childs = childs;
	}

	public void addChild(Term child) {
		if (childs == null) {
			childs = new ArrayList<Term>();
		}
		if (!contains(child)) {
			childs.add(child);
		}
	}

	public boolean contains(Term child) {
		for (Term oldChild : childs) {
			if (oldChild.getTerm().equalsIgnoreCase(child.getTerm())) {
				oldChild.incrementFrequency();
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "parent[" + parent + "], childs[" + childs + "]";
	}
}
