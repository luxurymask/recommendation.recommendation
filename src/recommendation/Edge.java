package recommendation;

public class Edge {
	private int edgeId;
	private Node sourceNode;
	private Node targetNode;
	
	public void setEdgeId(int edgeId){
		this.edgeId = edgeId;
	}
	
	public int getEdgeId(){
		return edgeId;
	}
	
	public void setSourceTarget(Node sourceNode, Node targetNode){
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
	}
	
	public Node getSourceNode(){
		return this.sourceNode;
	}
	
	public Node getTargetNode(){
		return this.targetNode;
	}
}
