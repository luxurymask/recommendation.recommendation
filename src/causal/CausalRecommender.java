package causal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;

public class CausalRecommender {
	private Map<String, Map<String, List<String>>> qcqMap = new HashMap<>();
	
	public CausalRecommender(String qcqFilePath){
		File qcqFile = new File(qcqFilePath);
		BufferedReader reader;
		try{
			reader = new BufferedReader(new FileReader(qcqFile));
			String s;
			while((s = reader.readLine()) != null){
				String[] qcqArray = s.split("\t");
				String firstQuery = qcqArray[0];
				String click = qcqArray[1];
				String secondQuery = qcqArray[2];
				if(qcqMap.containsKey(firstQuery)){
					Map<String, List<String>> cqMap = qcqMap.get(firstQuery);
					if(cqMap.containsKey(click)){
						List<String> qList = cqMap.get(click);
						qList.add(secondQuery);
					}else{
						cqMap.put(click, new ArrayList<String>());
						cqMap.get(click).add(secondQuery);
					}
				}else{
					qcqMap.put(firstQuery, new HashMap<String, List<String>>());
					qcqMap.get(firstQuery).put(click, new ArrayList<String>());
					qcqMap.get(firstQuery).get(click).add(secondQuery);
				}
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<String> recommend(String query){
		List<String> resultList = new ArrayList<>();
		if(qcqMap.containsKey(query)){
			for(Map.Entry<String, List<String>> entry : qcqMap.get(query).entrySet()){
				resultList.addAll(entry.getValue());
			}
			return resultList;
		}
		
		List<String> subTextList = getSubTexts(query);
		String maxQuery = "";
		int maxMatching = 0;
		for(Map.Entry<String, Map<String, List<String>>> entry : qcqMap.entrySet()){
			String firstQuery = entry.getKey();
			List<String> firstQuerySubTextList = getSubTexts(firstQuery);
			int matching = 0;
			for(String s : firstQuerySubTextList){
				if(subTextList.contains(s)) matching++;
			}
			if(maxMatching < matching){
				maxMatching = matching;
				maxQuery = firstQuery;
			}
		}
		
		if(maxQuery.equals("")) return new ArrayList<>();
		for(Map.Entry<String, List<String>> entry : qcqMap.get(maxQuery).entrySet()){
			resultList.addAll(entry.getValue());
		}
		return resultList;
	}
	
	private List<String> getSubTexts(String s){
		Result result = DicAnalysis.parse(s);
		StopRecognition filter = new StopRecognition();
		List<String> filterWords = new ArrayList<>();
		filterWords.add("———");
		filterWords.add("的");
		filterWords.add("和");
		filterWords.add("中");
		filterWords.add("及");
		filterWords.add("对");
		filterWords.add("有");
		filterWords.add(" ");
		filterWords.add("‘");
		filterWords.add("’");
		filterWords.add("'");
		filterWords.add("/");
		filter.insertStopWords(filterWords);
		// System.out.println(result.recognition(filter).toStringWithOutNature());
		String[] subTexts = result.toStringWithOutNature().split(",");
		List<String> resultList = new ArrayList<>();
		for(int i = 0;i < subTexts.length;i++){
			resultList.add(subTexts[i]);
		}
		return resultList;
	}
}
