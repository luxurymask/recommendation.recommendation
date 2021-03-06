package thematic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ThematicRecomender {

	private List<Graph> originalGraphList;

	private Synthesizer synthesizer;

	public ThematicRecomender(String seedQuery, String graphMLFileFolderPath, String clusterResultFolderPath) {
		originalGraphList = new ArrayList<>();
		int graphCount = getOriginalGraphList(graphMLFileFolderPath, clusterResultFolderPath);
		synthesizer = new Synthesizer(seedQuery, graphCount);
		synthesizer.synthesize(originalGraphList);
	}

	public Synthesizer getSynthesizer() {
		return synthesizer;
	}

	/**
	 * graph初始化，每个graph为一个子任务子图，不一定连通。
	 * 
	 * @param graphMLFileFolderPath
	 * @param clusterResultFolderPath
	 * @return 平均子任务个数。
	 */
	private int getOriginalGraphList(String graphMLFileFolderPath, String clusterResultFolderPath) {
		int graphCount = 0;
		File graphMLFileFolder = new File(graphMLFileFolderPath);
		File[] fileList = graphMLFileFolder.listFiles();
		int fileCount = 0;
		for (File graphMLFile : fileList) {
			String graphMLFilePath = graphMLFile.getAbsolutePath();
			if (graphMLFilePath.endsWith(".xml")) {
				fileCount++;
				Map<String, GraphNode> contentNodeMap = new HashMap<>();
				Map<String, GraphNode> idNodeMap = new HashMap<>();

				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(graphMLFile);
					// 读取节点信息。
					NodeList nodeList = doc.getElementsByTagName("node");
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						if (node instanceof Element) {
							String nodeType = ((Element) node).getAttribute("type");
							String nodeId = ((Element) node).getAttribute("id");
							String nodeContent = "";
							if (nodeType.equals("1")) {
								nodeContent = ((Element) node).getElementsByTagName("queryText").item(0)
										.getTextContent();
							} else if (nodeType.equals("2")) {
								nodeContent = ((Element) node).getElementsByTagName("title").item(0).getTextContent();
							} else {

							}
							GraphNode graphNode = new GraphNode(IdComputer.instance.getNodeId(), nodeType,
									nodeContent.trim().toLowerCase());
							idNodeMap.put(nodeId, graphNode);
							contentNodeMap.put(nodeContent.trim().toLowerCase(), graphNode);
						}
					}

					// 读取边信息。
					NodeList edgeList = doc.getElementsByTagName("edge");
					for (int i = 0; i < edgeList.getLength(); i++) {
						Node edge = edgeList.item(i);
						if (edge instanceof Element) {
							String sourceId = ((Element) edge).getAttribute("source");
							String targetId = ((Element) edge).getAttribute("target");
							GraphNode sourceNode = idNodeMap.get(sourceId);
							GraphNode targetNode = idNodeMap.get(targetId);
							GraphEdge graphEdge = new GraphEdge(IdComputer.instance.getEdgeId(), sourceNode,
									targetNode);
							sourceNode.addOutEdge(graphEdge);
							targetNode.addInEdge(graphEdge);
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}

				String name = graphMLFile.getName();
				String clusterResultFilePath = clusterResultFolderPath + name.split("\\.")[0] + ".txt";
				File clusterResultFile = new File(clusterResultFilePath);

				BufferedReader reader;
				int group = 0;
				try {
					reader = new BufferedReader(new FileReader(clusterResultFile));
					String s;
					Graph graph = new Graph();
					while ((s = reader.readLine()) != null) {
						s = s.trim().toLowerCase();
						String reg = "^-.*";
						if (s.matches(reg)) {
							group++;
							originalGraphList.add(graph);
							graph = new Graph();
							continue;
						}
						GraphNode currentNode = contentNodeMap.get(s);
						if (currentNode == null)
							throw new Exception("Wrong key word : " + s);
						graph.addNode(currentNode, "1", Graph.SIMILARITY_THRESHOLD);
						List<GraphEdge> currentOutEdgeList = currentNode.getOutEdgeList();
						for (GraphEdge graphEdge : currentOutEdgeList) {
							GraphNode targetNode = graphEdge.getTargetNode();
							if (targetNode.getNodeType().equals("2") && !graph.getNodeList().contains(targetNode)) {
								graph.addNode(targetNode, "1", Graph.SIMILARITY_THRESHOLD);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				graphCount += group;
			}
		}
		graphCount /= fileCount;
		return graphCount;
	}

	public List<String> recommendWithinTask() {

		double[] nodeRank = synthesizer.getGraphWithSeedQuery().computeNodeRank();
		
		if(nodeRank.length == 0) return new ArrayList<>();
		double max = 0;
		int maxI = 0;
		for (int i = 0; i < nodeRank.length; i++) {
			if (max < nodeRank[i]
					&& synthesizer.getGraphWithSeedQuery().getNodeList().get(i).getNodeType().equals("1")) {
				max = nodeRank[i];
				maxI = i;
			}
		}
		return synthesizer.getGraphWithSeedQuery().getNodeList().get(maxI).getContentList();
	}
	
	public List<List<String>> recommendWithoutTask(){
		List<List<String>> resultList = new ArrayList<>();
		for(Graph graph : synthesizer.getGraphsWithoutSeedQuery()){
			double[] nodeRank = graph.computeNodeRank();

			double max = 0;
			int maxI = 0;
			for (int i = 0; i < nodeRank.length; i++) {
				if (max < nodeRank[i]
						&& graph.getNodeList().get(i).getNodeType().equals("1")) {
					max = nodeRank[i];
					maxI = i;
				}
			}
			resultList.add(graph.getNodeList().get(maxI).getContentList());
		}
		
		return resultList;
	}
}
