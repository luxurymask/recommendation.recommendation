package conventional;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import accuracy.Util;

public class ConventionalRecommender {
	private Map<String, String> contentTimestampMap;
	
	public ConventionalRecommender(String graphMLPath){
		contentTimestampMap = Util.getQueryClickTimeLine(graphMLPath);
	}
	
	public String conventionalRecommend(String s){
		boolean flag = false;
		
		String match = "";
		double maxMatching = 0;
		for(Map.Entry<String, String> entry : contentTimestampMap.entrySet()){
			String content = entry.getKey();
			String reg = "^-.*";
			if (content.matches(reg)) {
				continue;
			}
			
			double matching;
			if((matching = Util.querySimulator(content, s)) > maxMatching){
				maxMatching = matching;
				match = content;
			}
		}
		
		for(Map.Entry<String, String> entry : contentTimestampMap.entrySet()){
			String content = entry.getKey();
			String reg = "^-.*";
			if (content.matches(reg)) {
				continue;
			}
			
			if(content.equals(match)){
				flag = true;
				continue;
			}
			
			if(flag == true){
				return content;
			}
		}
		return "";
	}
}
