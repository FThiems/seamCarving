package seamCarving;

public class Test
{
   static boolean visite[];
   public static void dfs(Graph g, int u)
	 {
		visite[u] = true;
		System.out.println("Je visite " + u);
		for (Edge e: g.next(u))
		  if (!visite[e.to])
			dfs(g,e.to);
	 }
   
   public static void testGraph()
	 {
		int n = 5;
		int i,j;
		GraphArrayList g = new GraphArrayList(n*n+2);
		
		for (i = 0; i < n-1; i++)
		  for (j = 0; j < n ; j++)
			g.addEdge(new Edge(n*i+j, n*(i+1)+j, 1664 - (i+j)));

		for (j = 0; j < n ; j++)		  
		  g.addEdge(new Edge(n*(n-1)+j, n*n, 666));
		
		for (j = 0; j < n ; j++)					
		  g.addEdge(new Edge(n*n+1, j, 0));
		
		g.addEdge(new Edge(13,17,1337));
		g.writeFile("test.dot");
		// dfs à partir du sommet 3
		visite = new boolean[n*n+2];
		dfs(g, 3);
	 }

	 public static void testWrite(){
		 SeamCarving sc = new SeamCarving();
//   		int[][] image = {{8,13,12,124,67},{4,255,123,167,200},{14,45,90,56,140}};
		int[][] image = sc.readpgm("ex1.pgm");
   		String filename = "test.pgm";
   		sc.writepgm(image, filename);
	 }

	 public static void testInterest(){
//   		SeamCarving sc = new SeamCarving();
//   		int[][] image = {{3,11,24,39},{8,21,29,39},{200,60,25,0}};
		 int[][] image = SeamCarving.readpgm("test.pgm");
   		int[][] itr = SeamCarving.interest(image);
   		for (int i = 0; i< itr.length; i++){
   			for(int j = 0; j<itr[0].length; j++){
				System.out.print(itr[i][j] +  " ");
			}
			System.out.println("");
		}
	 }

	 public static void testConstruGraph(){
   		int[][] image = SeamCarving.readpgm("ex1.pgm");
   		int[][] itr = SeamCarving.interest(image);
   		Graph g = SeamCarving.toGraph(itr);
   		int[] truePath = SeamCarving.bellman_Ford(g,itr.length*itr[0].length + 1,itr.length*itr[0].length);
   		for(int j = 0; j<truePath.length; j++){
			System.out.println(truePath[j]);
		}
		g.writeFile("test2.dot");
   }

   public static void main(String[] args)
	 {
//		testGraph();
//		testWrite();
//		testInterest();
		testConstruGraph();
	 }
}
