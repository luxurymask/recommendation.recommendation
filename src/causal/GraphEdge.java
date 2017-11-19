package causal;

public class GraphEdge {
	private int edgeId;
	private GraphNode sourceNode;
	private GraphNode targetNode;
	
	public GraphEdge(int edgeId, GraphNode sourceNode, GraphNode targetNode){
		this.edgeId = edgeId;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
	}
	
	public int getEdgeId(){
		return edgeId;
	}
	
	public void setSourceNode(GraphNode sourceNode){
		this.sourceNode = sourceNode;
	}
	
	public GraphNode getSourceNode(){
		return this.sourceNode;
	}
	
	public void setTargetNode(GraphNode targetNode){
		this.targetNode = targetNode;
	}
	
	public GraphNode getTargetNode(){
		return this.targetNode;
	}
}
