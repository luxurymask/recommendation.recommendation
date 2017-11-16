package recommendation;

import java.util.ArrayList;
import java.util.List;

public class Synthesizer {
	private String seedQuery;

	private Graph graphWithSeedQuery;

	private List<Graph> graphsWithoutSeedQuery;

	public Synthesizer(String seedQuery) {
		this.seedQuery = seedQuery;
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
}
