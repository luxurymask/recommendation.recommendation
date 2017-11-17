package recommendation;

import java.util.ArrayList;
import java.util.List;

public class Synthesizer {
	private String seedQuery;
	
	private int graphCount;

	private Graph graphWithSeedQuery;

	private List<Graph> graphsWithoutSeedQuery;

	public Synthesizer(String seedQuery, int graphCount) {
		this.seedQuery = seedQuery;
		this.graphCount = graphCount;
		graphWithSeedQuery = new Graph();
		graphsWithoutSeedQuery = new ArrayList<>();

	}

	public void synthesize(List<Graph> originalGraphList) {
		// 合并含有seedQuery的子任务子图
		for (Graph subTaskGraph : originalGraphList) {
			if (!subTaskGraph.containsQuery(seedQuery))
				continue;
			List<GraphNode> subTaskNodeList = subTaskGraph.getNodeList();
			List<GraphEdge> subTaskEdgeList = subTaskGraph.getEdgeList();
			for (GraphNode subTaskNode : subTaskNodeList) {
				graphWithSeedQuery.addNode(subTaskNode);
			}
			for (GraphEdge subTaskEdge : subTaskEdgeList) {
				graphWithSeedQuery.addEdge(subTaskEdge);
			}
		}
		
		//合并不含有seedQuery的子任务子图
		
	}
	
	public double computeSimilarity(Graph graph1, Graph graph2){
		List<GraphNode> graphNodeList1 = graph1.getNodeList();
		List<GraphNode> graphNodeList2 = graph2.getNodeList();
		double sum1 = 0;
		for(GraphNode graphNode1 : graphNodeList1){
			double max = 0;
			for(GraphNode graphNode2 : graphNodeList2){
				double similarity = graphNode1.compuleNodeSimularity(graphNode2);
				if(max < similarity) max = similarity;
			}
			sum1 += max;
		}
		
		sum1 /= graph1.getNodeList().size();
		
		double sum2 = 0;
		for(GraphNode graphNode2 : graphNodeList2){
			double max = 0;
			for(GraphNode graphNode1 : graphNodeList1){
				double similarity = graphNode2.compuleNodeSimularity(graphNode1);
				if(max < similarity) max = similarity;
			}
			sum2 += max;
		}
		
		sum2 /= graph2.getNodeList().size();
		
		return (sum1 + sum2) / 2;
	}
}
