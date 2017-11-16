package recommendation;

import java.util.ArrayList;
import java.util.List;

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
}
