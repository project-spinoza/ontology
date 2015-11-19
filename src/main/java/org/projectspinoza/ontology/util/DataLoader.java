package org.projectspinoza.ontology.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class DataLoader {
	private static Logger log = LogManager.getRootLogger();

	public static List<String> loadData(String filePath) {
		int counter = 0;
		BufferedReader br = null;
		List<String> data = null;
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			data = new ArrayList<String>();
			String line = null;
			while ((line = br.readLine()) != null) {
				if ((line = line.trim()).length() < 2) {
					continue;
				}
				data.add(line);
				counter++;
			}
			log.info("Rows loaded from " + filePath + "[" + counter + "]");
			br.close();
		} catch (IOException ex) {
			log.error("Error while reading file [" + filePath + "]");
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				log.error("Error while closing bufferedReader for [" + filePath
						+ "]");
			}
		}
		return data;
	}

	public static List<String> fetchTags(String filePath) {
		Set<String> tags = new HashSet<String>();
		List<String> data = loadData(filePath);
		int tagCounter = 0;
		if (data == null || data.isEmpty()) {
			log.error("No tags are found in [" + filePath + "]");
			return null;
		}
		for (String row : data) {
			String tweetText = fetchTweetText(row);
			if (tweetText == null || tweetText.length() < 3) {
				continue;
			}
			String[] terms = tweetText.split(" ");
			if (terms.length == 0) {
				continue;
			}
			for (String term : terms) {
				if (term == null) {
					continue;
				}
				term = term.replaceAll("[^a-zA-Z0-9#\\-_]", "").toLowerCase();
				if (term.length() < 2) {
					continue;
				}

				if (term.startsWith("#")) {
					int numHashs = term.length()
							- term.replaceAll("#", "").length();
					if (numHashs == 1) {
						tags.add(term.substring(1));
						tagCounter++;
					} else if (numHashs > 1) {
						String[] jointTerms = term.split("#");
						for (String jointTerm : jointTerms) {
							if (jointTerm.length() > 0) {
								tags.add(jointTerm);
								tagCounter++;
							}
						}
					}
				}
			}
		}
		log.info("HashTags Loaded [" + tagCounter + "]");
		return new ArrayList<String>(tags);
	}

	public static List<Map<String, String>> fetchOntologies(String filePath) {
		List<String> listJson = loadData(filePath);
		String json = listJson.size() == 1 ? listJson.get(0) : null;
		List<Map<String, String>> ontologies = new ArrayList<Map<String, String>>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			ontologies = mapper.readValue(json,
					new TypeReference<ArrayList<Map<String, String>>>() {
					});
			log.info("Ontologies Loaded [" + ontologies.size() + "]");
		} catch (Exception e) {
			log.error("Cannot fetch ontologies from [" + filePath);
			e.printStackTrace();
		}
		return ontologies;
	}

	public static String fetchTweetText(String json) {
		Map<String, Object> map = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(json,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (Exception e) {
			log.error("Cannot convert to map [" + json);
			e.printStackTrace();
		}
		if (map == null) {
			return null;
		}
		return map.get("text").toString();
	}
}
