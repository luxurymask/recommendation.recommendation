package causal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
	private List<GraphNode> nodeList = new ArrayList<>();
	private List<GraphEdge> edgeList = new ArrayList<>();
	private Map<String, GraphNode> contentNodeMap = new HashMap<>();
	public static double SIMILARITY_THRESHOLD = 0.5;
	
	/**
	 * 向图中添加节点。
	 * @param graphNode 要加入的节点
	 * @param percision 精确度：percision = "1"时完全匹配，否则按照节点相似度大于阈值的最大值匹配。
	 * @param threshold 阈值
	 */
	public void addNode(GraphNode graphNode, String percision, double threshold){
		List<String> contentList = graphNode.getContentList();
		for(String content : contentList){
			if(percision.equals("1")){
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
						//处理边
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
						return;
					}
				}
			}else{
				double maxSimularity = 0;
				GraphNode maxThisGraphNode = null;
				for(GraphNode thisGraphNode : nodeList){
					if(!thisGraphNode.getNodeType().equals(graphNode.getNodeType())) continue;
					double simularity = graphNode.compuleNodeSimularity(thisGraphNode);
					if(maxSimularity < simularity){
						maxSimularity = simularity;
						maxThisGraphNode = thisGraphNode;
					}
				}
				if(maxSimularity > threshold && maxThisGraphNode != null){
					maxThisGraphNode.combineNode(graphNode);
					//将不重复的词加入contentNodeMap。
					for(String content2 : contentList){
						if(!contentNodeMap.containsKey(content2)){
							contentNodeMap.put(content2, maxThisGraphNode);
						}
					}
					//处理边。
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
	
	/**
	 * 剪掉连向外界的边。
	 */
	public void cutEdge(){
		for(GraphEdge graphEdge : edgeList){
			GraphNode sourceNode = graphEdge.getSourceNode();
			GraphNode targetNode = graphEdge.getTargetNode();
			if(!nodeList.contains(sourceNode) || !nodeList.contains(targetNode)){
				try {
					sourceNode.removeOutEdge(graphEdge);
					targetNode.removeInEdge(graphEdge);
					edgeList.remove(graphEdge);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
