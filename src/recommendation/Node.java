package recommendation;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private int nodeId;
	
	private List<String> queryList = new ArrayList<>();
	
	private List<Edge> inEdgeList = new ArrayList<>();
	
	private List<Edge> outEdgeList = new ArrayList<>();
	
	public void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}
	
	public int getNodeId(){
		return nodeId;
	}
	
	
}
