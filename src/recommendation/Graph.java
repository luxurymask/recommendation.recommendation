package recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	private List<GraphNode> nodeList = new ArrayList<>();
	private List<GraphEdge> edgeList = new ArrayList<>();
	private Map<String, GraphNode> contentNodeMap = new HashMap<>();
	
	public void addNode(GraphNode graphNode){
		List<String> contentList = graphNode.getContentList();
		for(String content : contentList){
			if(contentNodeMap.containsKey(content)){
				GraphNode thisGraphNode = contentNodeMap.get(content);
				if(thisGraphNode.getNodeType().equals(graphNode.getNodeType())){
					thisGraphNode.combineNode(graphNode);
					//将不重复的词加入contentNodeMap。
					for(String content2 : contentList){
						if(!contentNodeMap.containsKey(content2)){
							contentNodeMap.put(content2, thisGraphNode);
						}
					}
					return;
				}
			}
		}
		
		nodeList.add(graphNode);
		for(GraphEdge graphEdge : graphNode.getInEdgeList()){
			if(!edgeList.contains(graphEdge)){
				edgeList.add(graphEdge);
			}
		}
		
		for(GraphEdge graphEdge : graphNode.getOutEdgeList()){
			if(!edgeList.contains(graphEdge)){
				edgeList.add(graphEdge);
			}
		}
		
		for(String content : contentList){
			contentNodeMap.put(content, graphNode);
		}
	}
	
	public void addEdge(GraphEdge edge){
		if(!edgeExists(edge)){
			edgeList.add(edge);
		}
	}
	
	public boolean edgeExists(String sourceContent, String targetContent){
		for(GraphEdge edge : edgeList){
			GraphNode sourceNode = edge.getSourceNode();
			GraphNode targetNode = edge.getTargetNode();
			return sourceNode.getContentList().contains(sourceContent) && targetNode.getContentList().contains(targetContent);
		}
		return false;
	}
	
	public boolean edgeExists(GraphEdge edge){
		int edgeId = edge.getEdgeId();
		for(GraphEdge thisEdge : edgeList){
			if(thisEdge.getEdgeId() == edgeId) return true;
		}
		return false;
	}
	
	public List<GraphNode> getNodeList(){
		return this.nodeList;
	}
	
	public List<GraphEdge> getEdgeList(){
		return this.edgeList;
	}
	
	public boolean containsQuery(String seedQuery){
		return contentNodeMap.containsKey(seedQuery);
	}
}
