package thematic;

public class PageRankBasic {
	private static double ALPHA = 0.8;
	
	public static double[] pageRank(int n, boolean[][] graph){
		double[] nowRank = new double[n];
		double[] resultRank = new double[n];
		int[] outCount = new int[n];
		for(int i = 0;i < n;i++){
			for(int j = 0;j < n;j++){
				if(graph[j][i] == true){
					outCount[i]++;
				}
			}
		}
		double[][] arrayS = new double[n][n];
		for(int i = 0;i < n;i++){
			for(int j = 0;j < n;j++){
				if(graph[j][i] == true){
					arrayS[j][i] = 1.0 / outCount[i];
				}
			}
		}
		
		for(int i = 0;i < n;i++){
			nowRank[i] = 1.0 / n;
		}
		
		boolean flag = false;
		for(int count = 0;flag == false && count < 10;count++){
			for(int i = 0;i < n;i++){
				resultRank[i] = 0;
				for(int j = 0;j < n;j++){
					resultRank[i] += arrayS[i][j] * nowRank[j];
				}
				resultRank[i] *= ALPHA;
				resultRank[i] += (1 - ALPHA) * (1.0 / n);
			}
			for(int i = 0;i < n;i++){
				if(nowRank[i] == resultRank[i]){
					flag = true;
				}else{
					flag = false;
					nowRank[i] = resultRank[i];
				}
			}
		}
		
		return resultRank;
	}
	
	public static void main(String[] args){
		boolean[][] graph = new boolean[][]{{false,true,false,false},{true,false,false,true},{true,false,true,true},{true,true,false,false}};
		double[] rank = pageRank(4, graph);
		for(int i = 0;i < 4;i++){
			System.out.println(rank[i]);
		}
	}
}
