package org.projectspinoza.ontology;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.projectspinoza.ontology.util.DataLoader;
import org.projectspinoza.ontology.util.Term;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DataLoader.class })
public class TestTermOntologyMatcher {
	Term term;
	TermOntologyMatcher tm = new TermOntologyMatcher("tweets.txt",
			"ontologies.json");
	List<String> tweetTags = new ArrayList<String>();
	Map<String, String> ontology = new HashMap<String, String>();
	List<Map<String, String>> ontologies = new ArrayList<Map<String, String>>();

	@Before
	public void setup() {
		
		PowerMockito.mockStatic(DataLoader.class);
		tweetTags.add("car");
		tweetTags.add("2014rlx");
		tweetTags.add("acura");

		ontology.put("Title", "Hartley Brody");
		ontology.put("Tag", "technology, Sports, car, acura, hartleybrody");
		ontology.put("Body", "This is technology");
		ontology.put("Parent", "Technology");
		ontologies.add(ontology);

		term = new Term("Technology");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void TestaddToRelation(){
		List<Term> expected = new ArrayList<Term>();
		expected.add(term);
		List<Term> matches = (List<Term>) tm.matchTerms("tweets.txt",
				"ontologies.json").get("matched");
		List<Term> result = tm.addToRelation("technology", ontology, matches);
		
		assertEquals(expected.get(0).getTitle(), result.get(0).getTitle());
		assertEquals(expected.get(0).getTerm(), result.get(0).getTerm());
		assertEquals(expected.get(0).getDescription(), result.get(0)
				.getDescription());
		assertEquals(expected.get(0).getFrequency(), result.get(0)
				.getFrequency());
		assertEquals(expected.get(0).getOverAllFrequency(), result.get(0)
				.getOverAllFrequency());
		
	}

	@SuppressWarnings("unchecked")
	@Test
	public void TestMatchTerms() {
		List<Term> expected = new ArrayList<Term>();
		expected.add(term);
		PowerMockito.when(DataLoader.fetchTags("tweets.txt")).thenReturn(
				tweetTags);
		PowerMockito.when(DataLoader.fetchOntologies("ontologies.json"))
				.thenReturn(ontologies);
		List<Term> result = (List<Term>) tm.matchTerms("tweets.txt",
				"ontologies.json").get("matched");
		
		assertEquals(expected.get(0).getTitle(), result.get(0).getTitle());
		assertEquals(expected.get(0).getTerm(), result.get(0).getTerm());
		assertEquals(expected.get(0).getDescription(), result.get(0)
				.getDescription());
		assertEquals(expected.get(0).getFrequency(), result.get(0)
				.getFrequency());
		assertEquals(expected.get(0).getOverAllFrequency(), result.get(0)
				.getOverAllFrequency());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void TestUnMatchTerms() {
		List<String> expected = new ArrayList<String>();
		expected.add("2014rlx");
		PowerMockito.when(DataLoader.fetchTags("tweets.txt")).thenReturn(
				tweetTags);
		PowerMockito.when(DataLoader.fetchOntologies("ontologies.json"))
				.thenReturn(ontologies);

		List<String> result = (List<String>) tm.matchTerms("tweets.txt",
				"ontologies.json").get("unMatched");

		assertEquals(expected, result);

	}
	
}
