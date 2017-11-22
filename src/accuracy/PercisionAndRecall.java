package accuracy;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import causal.CausalRecommender;
import conventional.ConventionalRecommender;
import thematic.ThematicRecomender;

public class PercisionAndRecall {
	private Set<String> querySet = new HashSet<>();
	private Set<String> thematicWithinTaskQuerySet = new HashSet<>();
	private Set<String> thematicWithoutTaskQuerySet = new HashSet<>();
	private Set<String> causalQuerySet = new HashSet<>();
	private Set<String> conventionalQuerySet = new HashSet<>();
	
	private int count = 0;

	public PercisionAndRecall(String testGraphMLFilePath) {
		File testGraphMLFile = new File(testGraphMLFilePath);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(testGraphMLFile);
			// 读取节点信息。
			NodeList nodeList = doc.getElementsByTagName("node");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node instanceof Element) {
					String nodeType = ((Element) node).getAttribute("type");
					if (nodeType.equals("2"))
						continue;
					String nodeContent = ((Element) node).getElementsByTagName("queryText").item(0).getTextContent()
							.trim().toLowerCase();
					querySet.add(nodeContent);
					thematicWithinTaskQuerySet.add(nodeContent);
					thematicWithoutTaskQuerySet.add(nodeContent);
					causalQuerySet.add(nodeContent);
					conventionalQuerySet.add(nodeContent);
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double[] computePercisionAndRecall(String task) {
		String graphMLFileFolderPath = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/训练集/";
		String clusterResultFolderPath = "/Users/liuxl/Desktop/recommendation/phase4/cluster result/tree/人工/" + task
				+ "/";
		String qcqFilePath = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/训练集/qcq.txt";
		String graphMLFilePath1 = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/训练集/1.xml";
		String graphMLFilePath2 = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/训练集/2.xml";

		int thematicWithinTaskCount = 0;
		int thematicWithoutTaskCount = 0;
		int causalCount = 0;
		int conventionalCount = 0;

		int thematicWithinTaskSum = 0;
		int thematicWithoutTaskSum = 0;
		int causalSum = 0;
		int conventionalSum = 0;

		int thematicWithinTaskRecallCount = 0;
		int thematicWithoutTaskRecallCount = 0;
		int causalRecallCount = 0;
		int conventionalRecallCount = 0;

		CausalRecommender causalRecommender = new CausalRecommender(qcqFilePath);
		ConventionalRecommender conventionalRecommender1 = new ConventionalRecommender(graphMLFilePath1);
		ConventionalRecommender conventionalRecommender2 = new ConventionalRecommender(graphMLFilePath2);
		for (String query : querySet) {
			System.out.println("Basing " + query + " computing recommendation...");
			ThematicRecomender thematicRecommender = new ThematicRecomender(query, graphMLFileFolderPath,
					clusterResultFolderPath);
			List<String> recommendWithinTaskList = thematicRecommender.recommendWithinTask();
			List<List<String>> recommendWithoutTaskList = thematicRecommender.recommendWithoutTask();

			List<String> causalList = causalRecommender.recommend(query);
			String conventionalRecommend1 = conventionalRecommender1.conventionalRecommend(query);
			String conventionalRecommend2 = conventionalRecommender2.conventionalRecommend(query);

			//准确率
			for (String s : recommendWithinTaskList) {
				for (String ss : querySet) {
					if (Util.querySimulator(ss, s) > Util.THRESHOLD) {
						thematicWithinTaskCount++;
						break;
					}
				}
				thematicWithinTaskSum++;
			}

			for (List<String> list : recommendWithoutTaskList) {
				for (String s : list) {
					for (String ss : querySet) {
						if (Util.querySimulator(ss, s) > Util.THRESHOLD) {
							thematicWithoutTaskCount++;
							break;
						}
					}
					thematicWithoutTaskSum++;
				}
			}

			for (String s : causalList) {
				for (String ss : querySet) {
					if (Util.querySimulator(ss, s) > Util.THRESHOLD) {
						causalCount++;
						break;
					}
				}
				causalSum++;
			}

			for (String ss : querySet) {
				if (Util.querySimulator(ss, conventionalRecommend1) > Util.THRESHOLD) {
					conventionalCount++;
					break;
				}
				if (Util.querySimulator(ss, conventionalRecommend2) > Util.THRESHOLD) {
					conventionalCount++;
					break;
				}
			}
			conventionalSum++;

			
			//召回率
			for (String s : recommendWithinTaskList) {
				String maxContent = "";
				double maxMatching = 0;
				for (String ss : thematicWithinTaskQuerySet) {
					double matching;
					if ((matching = Util.querySimulator(ss, s)) > maxMatching) {
						maxMatching = matching;
						maxContent = ss;
					}
				}
				
				if (maxMatching > Util.THRESHOLD) {
					thematicWithinTaskQuerySet.remove(maxContent);
				}
			}

			for (List<String> list : recommendWithoutTaskList) {
				for (String s : list) {
					String maxContent = "";
					double maxMatching = 0;
					for (String ss : thematicWithoutTaskQuerySet) {
						double matching;
						if ((matching = Util.querySimulator(ss, s)) > maxMatching) {
							maxMatching = matching;
							maxContent = ss;
						}
					}
					
					if (maxMatching > Util.THRESHOLD) {
						thematicWithoutTaskQuerySet.remove(maxContent);
					}
				}
			}

			for (String s : causalList) {
				String maxContent = "";
				double maxMatching = 0;
				for (String ss : causalQuerySet) {
					double matching;
					if ((matching = Util.querySimulator(ss, s)) > maxMatching) {
						maxMatching = matching;
						maxContent = ss;
					}
				}
				
				if (maxMatching > Util.THRESHOLD) {
					causalQuerySet.remove(maxContent);
				}
			}

			String maxContent = "";
			double maxMatching = 0;
			for (String ss : conventionalQuerySet) {
				double matching;
				if ((matching = Util.querySimulator(ss, conventionalRecommend1)) > maxMatching) {
					maxMatching = matching;
					maxContent = ss;
				}
				if ((matching = Util.querySimulator(ss, conventionalRecommend2)) > maxMatching) {
					maxMatching = matching;
					maxContent = ss;
				}
			}
			
			if (maxMatching > Util.THRESHOLD) {
				conventionalQuerySet.remove(maxContent);
			}
		}
		double[] result = new double[8];
		result[0] = (double) causalCount / causalSum;
		result[1] = (double) thematicWithinTaskCount / thematicWithinTaskSum;
		result[2] = (double) thematicWithoutTaskCount / thematicWithoutTaskSum;
		result[3] = (double) conventionalCount / conventionalSum;

		result[4] = (double) causalQuerySet.size() / count;
		result[5] = (double) thematicWithinTaskQuerySet.size() / count;
		result[6] = (double) thematicWithoutTaskQuerySet.size() / count;
		result[7] = (double) conventionalQuerySet.size() / count;
		return result;
	}
}
