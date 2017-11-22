package thematic;

public class IdComputer {
	public static IdComputer instance = new IdComputer();
	
	private int nodeId = 0;
	private int edgeId = 0;
	
	public int getNodeCount(){
		return nodeId;
	}
	
	public int getEdgeCount(){
		return edgeId;
	}
	
	public int getNodeId(){
		return nodeId++;
	}
	
	public int getEdgeId(){
		return edgeId++;
	}
}
