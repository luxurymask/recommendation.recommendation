package recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;

public class GraphNode {
	private int nodeId;
	
	private String nodeType;
	
	private List<String> contentList = new ArrayList<>();
	
	private List<GraphEdge> inEdgeList = new ArrayList<>();
	
	private List<GraphEdge> outEdgeList = new ArrayList<>();
	
	public GraphNode(int nodeId, String nodeType){
		this.nodeId = nodeId;
		this.nodeType = nodeType;
	}
	
	public GraphNode(int nodeId, String nodeType, String nodeContent){
		this.nodeId = nodeId;
		this.nodeType = nodeType;
		contentList.add(nodeContent);
	}
	
	public String getNodeType(){
		return nodeType;
	}
	
	public List<GraphEdge> getInEdgeList(){
		return this.inEdgeList;
	}
	
	public List<GraphEdge> getOutEdgeList(){
		return this.outEdgeList;
	}
	
	public void addInEdge(GraphEdge graphEdge){
		inEdgeList.add(graphEdge);
	}
	
	public void addOutEdge(GraphEdge graphEdge){
		outEdgeList.add(graphEdge);
	}
	
	public void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	public List<String> getContentList(){
		return contentList;
	}
	
	public int combineNode(GraphNode graphNode){
		List<String> contentList = graphNode.getContentList();
		for(String content : contentList){
			if(!this.contentList.contains(content)){
				this.contentList.add(content);
			}
		}
		
		List<GraphEdge> inEdgeList = graphNode.getInEdgeList();
		for(GraphEdge graphEdge : inEdgeList){
			graphEdge.setTargetNode(this);
			if(!this.inEdgeList.contains(graphEdge)){
				this.inEdgeList.add(graphEdge);
			}
		}
		
		List<GraphEdge> outEdgeList = graphNode.getOutEdgeList();
		for(GraphEdge graphEdge : outEdgeList){
			graphEdge.setSourceNode(this);
			if(!this.outEdgeList.contains(graphEdge)){
				this.outEdgeList.add(graphEdge);
			}
		}
		
		this.nodeId = Integer.min(this.nodeId, graphNode.getNodeId());
		return this.nodeId;
	}
	
	/**
	 * 计算节点间相似度，偷懒版，没用tf-idf。
	 * @param graphNode
	 * @return
	 */
	public double compuleNodeSimularity(GraphNode graphNode){
		int count = 0;
		int sum = 0;
		List<String> contentList = graphNode.getContentList();
		Map<String, Integer> subContentCountMap1 = new HashMap<>();
		Map<String, Integer> subContentCountMap2 = new HashMap<>();
		for(String content : this.contentList){
			String[] subTexts = getSubTexts(content);
			for(int i = 0;i < subTexts.length;i++){
				String subText = subTexts[i];
				if (!subText.equals("") && !subText.equals(" ")) {
					sum++;
					if(subContentCountMap1.containsKey(subText)){
						subContentCountMap1.put(subText, subContentCountMap1.get(subText) + 1);
					}else{
						subContentCountMap1.put(subText, 1);
					}
				}
			}
		}
		for(String content : contentList){
			String[] subTexts = getSubTexts(content);
			for(int i = 0;i < subTexts.length;i++){
				String subText = subTexts[i];
				if (!subText.equals("") && !subText.equals(" ")) {
					if(subContentCountMap2.containsKey(subText)){
						subContentCountMap2.put(subText, subContentCountMap2.get(subText) + 1);
					}else{
						subContentCountMap2.put(subText, 1);
					}
				}
			}
		}
		
		for(Map.Entry<String, Integer> entry1 : subContentCountMap1.entrySet()){
			String content1 = entry1.getKey();
			if(subContentCountMap2.containsKey(content1)){
				count++;
				if(subContentCountMap2.get(content1) == 0){
					subContentCountMap2.remove(content1);
				}else{
					subContentCountMap2.put(content1, subContentCountMap2.get(content1) - 1);
				}
			}
		}
		
		return (double)count / sum;
	}
	
	private String[] getSubTexts(String s){
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
		return subTexts;
	}
}
