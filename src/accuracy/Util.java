package accuracy;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util {
	public static double THRESHOLD = 0.62;
	
	public static double querySimulator(String query1, String query2) {
		List<String> subTextList1 = getSubTexts(query1);
		List<String> subTextList2 = getSubTexts(query2);

		Map<String, Integer> subTextCountMap1 = new HashMap<>();
		Map<String, Integer> subTextCountMap2 = new HashMap<>();

		for (String s : subTextList1) {
			if (subTextCountMap1.containsKey(s)) {
				subTextCountMap1.put(s, subTextCountMap1.get(s) + 1);
			} else {
				subTextCountMap1.put(s, 1);
			}
		}

		for (String s : subTextList2) {
			if (subTextCountMap2.containsKey(s)) {
				subTextCountMap2.put(s, subTextCountMap2.get(s) + 1);
			} else {
				subTextCountMap2.put(s, 1);
			}
		}

		int count = 0;
		for (Map.Entry<String, Integer> entry1 : subTextCountMap1.entrySet()) {
			String s = entry1.getKey();
			if (subTextCountMap2.containsKey(s)) {
				count++;
				if (subTextCountMap2.get(s) == 1) {
					subTextCountMap2.remove(s);
				} else {
					subTextCountMap2.put(s, subTextCountMap2.get(s) - 1);
				}
			}
		}

		return ((double) count / subTextList1.size() + (double) count / subTextList2.size()) / 2;
	}

	private static List<String> getSubTexts(String s) {
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
		for (int i = 0; i < subTexts.length; i++) {
			resultList.add(subTexts[i]);
		}
		return resultList;
	}

	public static Map<String, String> getQueryClickTimeLine(String inputPath) {
		File inputFile = new File(inputPath);
		Map<String, String> nodeTimestampMap = new HashMap<String, String>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);

			// 读取节点信息。
			NodeList nodeList = doc.getElementsByTagName("node");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node instanceof Element) {
					String nodeType = ((Element) node).getAttribute("type");
					NodeList vertexOperationList = ((Element) node).getElementsByTagName("vertexOperation");
					String timestamp = "";
					for (int j = 0; j < vertexOperationList.getLength(); j++) {
						Node vertexOperation = vertexOperationList.item(j);
						String operationType = ((Element) vertexOperation).getAttribute("type");
						if (operationType.equals("1")) {
							timestamp = ((Element) vertexOperation).getElementsByTagName("time").item(0)
									.getTextContent();
							break;
						}
					}
					if (nodeType.equals("1")) {
						String queryText = ((Element) node).getElementsByTagName("queryText").item(0).getTextContent();
						nodeTimestampMap.put(queryText, timestamp);
					} 
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		Map<String, String> resultMap = sortByValue(nodeTimestampMap);
		return resultMap;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		Stream<Entry<K, V>> st = map.entrySet().stream();

		st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));

		return result;
	}
}
