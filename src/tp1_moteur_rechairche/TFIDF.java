package tp1_moteur_rechairche;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import java.io.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.util.*;

import safar.basic.morphology.stemmer.factory.StemmerFactory;
import safar.basic.morphology.stemmer.interfaces.IStemmer;
import safar.basic.morphology.stemmer.model.StemmerAnalysis;
import safar.basic.morphology.stemmer.model.WordStemmerAnalysis;
import safar.util.remover.Remover;

public class TFIDF {

	// this function Read file content into string with - Files.lines(Path path,
	// Charset cs)

	public String readLineByLine(String filePath) {

		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuilder.toString();

	}

	// Remove StopWords from the ArrayList:
	public static ArrayList<String> deletStopWord(ArrayList<String> stopWords, List<String> arraylist) {

		ArrayList<String> NewList = new ArrayList<String>();
		int i = 0;
		while (i < arraylist.size()) {
			if (!stopWords.contains(arraylist.get(i))) {
				NewList.add((String) arraylist.get(i));
			}
			i++;
		}
		return NewList;
	}

	public double tf(List<String> doc, String term) {
		double result = 0;
		for (String word : doc) {
			if (word.equals(term))
				result++;
		}
		return (double) result / doc.size();
	}

	public double idf(List<Map<String, Integer>> listOfMaps, String term) {
		double df = 0;
		for (Map<String, Integer> map : listOfMaps) {
			for (Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getKey().equals(term)) {
					df++;
					break;
				}
			}
		}
		if (df == 0) {
			df = 1;
			return Math.log((double) listOfMaps.size() / df);
		}

		else
			return Math.log((double) listOfMaps.size() / df);

	}

	public ArrayList<String> ListOfStopWord() {

		File stopWordsPath = new File("Source/stopwords.txt");
		ArrayList<String> listStopWords = new ArrayList<String>();
		String[] arrayStpWords = readLineByLine(stopWordsPath.toString()).split("\\s");

		for (String str : arrayStpWords) {
			listStopWords.add(str);
		}

		return listStopWords;
	}

	public List<List<String>> readFiles(String path) {

		// specify the folder path witch contains all the texts files
		File directory = new File(path);
		String document = "";
		List<List<String>> textFiles = new ArrayList<List<String>>();
		List<String> doc;

		// store the text files in an ArrayList "textFiles" :
		for (File file : directory.listFiles()) {
			document = readLineByLine(file.toString());
			doc = Arrays.asList(document.split(" "));
			// doc=deletStopWord(v.ListOfStopWord(),doc);
			textFiles.add(doc);
		}
		return textFiles;
	}

	// Apply Stemming Algorithm:
	public List<Map<String, Integer>> stemingText(List<List<String>> inputList) { // inputList: represents a list of
																					// lists (each nested list is a
																					// document)

		IStemmer stemmer = StemmerFactory.getKhojaImplementation();
		ArrayList<String> stopWords = ListOfStopWord();

		// corpusMap : will contains the pairs (key,values) of all the stemmed terms in
		// the corpus :
		Map<String, Integer> corpusMap = null;
		// listOfMaps : will contains a List of Maps : each Map represent a stemmed
		// document.
		List<Map<String, Integer>> listOfMaps = new ArrayList<Map<String, Integer>>();

		int i = 0;
		while (i < inputList.size()) {
			// stem the text with the specified stemmer implementation and get result
			// we apply this algorithm to each documents in the corpus :"inputList"
			corpusMap = new HashMap<String, Integer>();
			List<WordStemmerAnalysis> listResult = stemmer.stem(inputList.get(i).toString());

			for (WordStemmerAnalysis wsa : listResult) {
				// "stem" :contain the first stem ?
				String stem = wsa.getListStemmerAnalysis().get(0).getMorpheme();
				if (!corpusMap.containsKey(stem)) {
					if (stem != null && stem.length() >= 2 && !stopWords.contains(stem)) {
						corpusMap.put(stem, 1);
					}
				} else {
					int k = corpusMap.get(stem);
					k++;
					corpusMap.put(stem, k);
				}
			}
			listOfMaps.add(corpusMap);
			// System.out.println(corpusMap);
			i++;
		}

		return listOfMaps;
	}

	public List<Map<String, Double>> listOfTFIDFs(List<Map<String, Integer>> listOfMaps_Occurs) {

		List<Map<String, Double>> listMapTFIDFs = new ArrayList<Map<String, Double>>();
		Map<String, Double> mapTFIDF = null;

		for (Map<String, Integer> map : listOfMaps_Occurs) {

			mapTFIDF = new HashMap<String, Double>();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {

				ArrayList<String> keyList = new ArrayList<String>(map.keySet());
				String key = entry.getKey();
				double IDF = idf(listOfMaps_Occurs, key);
				double tf = tf(keyList, key);
				mapTFIDF.put(key, IDF * tf);
			}
			listMapTFIDFs.add(mapTFIDF);
		}
		return listMapTFIDFs;
	}

	public List<Map<String, Double>> tfidfOfRequete(List<Map<String, Integer>> listOfMaps_Occurs,
			List<Map<String, Integer>> requete) {

		List<Map<String, Double>> listMap = new ArrayList<Map<String, Double>>();
		Map<String, Double> mapTFIDF = null;
		for (Map<String, Integer> map : requete) {
			mapTFIDF = new HashMap<String, Double>();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				ArrayList<String> keyList = new ArrayList<String>(map.keySet());
				String key = entry.getKey();
				double IDF = idf(listOfMaps_Occurs, key);
				double tf = tf(keyList, key);
				System.err.println("tf=" + tf(keyList, key) + " IDF =" + IDF + " term=" + key);
				mapTFIDF.put(key, IDF * tf);

			}
			listMap.add(mapTFIDF);
		}
		return listMap;
	}

	public Map<String, Double> cosineSimilarity_1(List<Map<String, Double>> listOfMapsTFIDFs,
			List<Map<String, Double>> requete) {
		double dotProduct;
		double normMapRequete;
		double normMapDocs;

		Map<String, Double> mapCosine = new HashMap<String, Double>();
		;
		Map<String, Double> rqt = requete.get(0);

		int doc_Num = 1;

		for (Map<String, Double> map : listOfMapsTFIDFs) {
			dotProduct = 0.0;
			normMapRequete = 0.0;
			normMapDocs = 0.0;
			boolean drapo = false;
			for (Entry<String, Double> entry_rqt : rqt.entrySet()) {

				for (Entry<String, Double> entry_maps : map.entrySet()) {
					if (entry_rqt.getKey().equals(entry_maps.getKey())) {
						dotProduct += entry_rqt.getValue() * entry_maps.getValue();
						normMapRequete += Math.pow(entry_rqt.getValue(), 2);
						normMapDocs += Math.pow(entry_maps.getValue(), 2);
						drapo = true;
						break;
					}

				}
			}
			if (drapo) {
				mapCosine.put("المادة " + doc_Num, dotProduct / (Math.sqrt(normMapRequete) * Math.sqrt(normMapDocs)));
			} else {
				mapCosine.put("المادة " + doc_Num, 0.0);
			}

			doc_Num++;
		}
		return mapCosine;
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1; // Special fix to preserve items with equal values
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}

	public Map<String, Double> tifidf_Search(TFIDF tf_idf, String str) {

		List<List<String>> textFiles = new ArrayList<List<String>>();
		textFiles = readFiles("Source/taamir");

		List<Map<String, Integer>> listOfMaps_Occurs = tf_idf.stemingText(textFiles);
		List<Map<String, Double>> listMapTFIDFs = tf_idf.listOfTFIDFs(listOfMaps_Occurs);

		// List<List<String> > textRequete =
		// tf_idf.readFiles("C:\\workspace-MIDVI-S3\\moteurDeRecherche\\Source\\requete");
		List<List<String>> textRequete = new ArrayList<List<String>>();
		List<String> doc = Arrays.asList(str.split(" "));

		textRequete.add(doc);

		List<Map<String, Integer>> listOfMapsRequete = tf_idf.stemingText(textRequete);
		List<Map<String, Double>> RequeteTFIDFs = tf_idf.tfidfOfRequete(listOfMaps_Occurs, listOfMapsRequete);

		Map<String, Double> mapCos = tf_idf.cosineSimilarity_1(listMapTFIDFs, RequeteTFIDFs);

		System.out.println("#########################################################");
		System.out.println(mapCos);

		Map<String, Double> mapCosFinal = new HashMap<String, Double>();
		for (Entry<String, Double> entry : entriesSortedByValues(mapCos)) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
			mapCosFinal.put(entry.getKey(), entry.getValue());
		}

		System.out.println(mapCosFinal);
		return mapCosFinal;

	}
}
