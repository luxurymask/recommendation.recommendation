package accuracy;

import java.util.Scanner;

public class Main {
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		String task = in.nextLine();
		String testGraphMLFilePath = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/测试集/3.xml";
		PercisionAndRecall percision1 = new PercisionAndRecall(testGraphMLFilePath);
		double[] result1 = percision1.computePercisionAndRecall(task);
		System.out.println("因果经验推荐准确率：" + result1[0]);
		System.out.println("子任务内部推荐准确率：" + result1[1]);
		System.out.println("跨子任务推荐准确率：" + result1[2]);
		System.out.println("传统经验推荐准确率：" + result1[3]);
		System.out.println("因果经验推荐召回率：" + result1[4]);
		System.out.println("子任务内部推荐召回率：" + result1[5]);
		System.out.println("跨子任务推荐召回率：" + result1[6]);
		System.out.println("传统经验推荐召回率：" + result1[7]);
		
		String testGraphMLFilePath2 = "/Users/liuxl/Desktop/recommendation/phase4/推荐实验数据/" + task + "/测试集/4.xml";

		PercisionAndRecall percision2 = new PercisionAndRecall(testGraphMLFilePath2);
		double[] result2 = percision2.computePercisionAndRecall(task);
		System.out.println("因果经验推荐准确率：" + result2[0]);
		System.out.println("子任务内部推荐准确率：" + result2[1]);
		System.out.println("跨子任务推荐准确率：" + result2[2]);
		System.out.println("传统经验推荐准确率：" + result2[3]);
		System.out.println("因果经验推荐召回率：" + result2[4]);
		System.out.println("子任务内部推荐召回率：" + result2[5]);
		System.out.println("跨子任务推荐召回率：" + result2[6]);
		System.out.println("传统经验推荐召回率：" + result2[7]);
	}
}
