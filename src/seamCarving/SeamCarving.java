package seamCarving;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class SeamCarving {

	public static int[][] readpgm(String fn) {
		try {
			InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
			BufferedReader d = new BufferedReader(new InputStreamReader(f));
			String magic = d.readLine();
			String line = d.readLine();
			while (line.startsWith("#")) {
				line = d.readLine();
			}
			Scanner s = new Scanner(line);
			int width = s.nextInt();
			int height = s.nextInt();
			line = d.readLine();
			s = new Scanner(line);
			int maxVal = s.nextInt();
			int[][] im = new int[height][width];
			s = new Scanner(d);
			int count = 0;
			while (count < height * width) {
				im[count / width][count % width] = s.nextInt();
				count++;
			}
			return im;
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 * Ecrit le fichier PGM
	 *
	 * @param image    tableau de l'image
	 * @param filename nom du fichier
	 */
	public static void writepgm(int[][] image, String filename) {
		FileWriter flot;
		PrintWriter flotFiltre;
		try {
			flot = new FileWriter(filename);
			flotFiltre = new PrintWriter(new BufferedWriter(flot));
			flotFiltre.println("P2");
			flotFiltre.println(image[0].length + " " + image.length);
			flotFiltre.println("255");
			for (int i = 0; i < image.length; i++) {
				for (int j = 0; j < image[i].length; j++) {
					flotFiltre.print(image[i][j] + " ");
				}
				flotFiltre.println("");
			}
			flotFiltre.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Fonction qui calcule et renvoie le tableau d'interet des pixels de l'image
	 *
	 * @param image dont l'interet des pixels est à calculer
	 * @return le tableau d'interet
	 */
	public static int[][] interest(int[][] image) {
		int[][] itr = new int[image.length][image[0].length];

		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[0].length; j++) {
				if (j == 0) {
					itr[i][j] = Math.abs(image[i][j] - image[i][j + 1]);
				} else if (j == image[0].length - 1) {
					itr[i][j] = Math.abs(image[i][j] - image[i][j - 1]);
				} else {
					itr[i][j] = Math.abs(image[i][j] - (image[i][j - 1] + image[i][j + 1]) / 2);
				}
			}
		}
		return itr;
	}

	/**
	 * Crée un graphe à partir du tableau d'interet
	 * @param itr
	 * @return
	 */
	public static Graph toGraph(int[][] itr) {
		GraphArrayList g = new GraphArrayList(itr.length * itr[0].length + 2);
		int height = itr.length;
		int len = itr[0].length;
//		 System.out.println(len + " " + height);
		int i, j;
		for (i = 0; i < height - 1; i++) {
			for (j = 0; j < len; j++) {
				g.addEdge(new Edge(len * i + j, len * (i + 1) + j, itr[i][j]));

				if (j != 0) {
					g.addEdge(new Edge(len * i + j, len * (i + 1) + j - 1, itr[i][j]));
				}
				if (j != len - 1) {
					g.addEdge(new Edge(len * i + j, len * (i + 1) + j + 1, itr[i][j]));
				}
			}
		}
		for (j = 0; j < len; j++) {
			g.addEdge(new Edge(len * height + 1, j, 0));
		}
		for (j = 0; j < len; j++) {
			g.addEdge(new Edge(len * height - len + j, len * height, itr[height - 1][j]));
		}
		return g;
	}

	/**
	 * Algo de Bellman-Ford
	 * @param g Graphe à tester
	 * @param s Sommet de départ
	 * @param t Sommet "arrivée"
	 * @return le tableau du plus court chemin
	 */
	public static int[] bellman_Ford(Graph g, int s, int t) {
		//Initialisations
		int[] distances = new int[g.vertices()];
		int[] parents = new int[g.vertices()];
		for (int i = 0; i < distances.length; i++) {
			distances[i] = Integer.MAX_VALUE;
			parents[i] = -1;
		}
		//On fait le traitement principal
		for(int i = 1; i<g.vertices(); i++){
			int[] copy = new int[distances.length];
			for(int k = 0; k<copy.length; k++){
				copy[k] = distances[k];
			}
			//On relâche les arcs
			for(Edge e : g.edges()){
//				System.out.println("Départ " + e.from + " Arrivée " + e.to + " Coût " + e.cost);
				if(distances[e.to] >= distances[e.from] + e.cost){
					distances[e.to] = distances[e.from] + e.cost;
					parents[e.to] = e.from;
				}
			}
			//Petit rajout pour éviter que l'algo tourne pour rien et mette plus de temps qu'il n'en faut pour trouver le chemin le plus court
			boolean same = true;
			for(int k = 0; k<distances.length; k++){
				if(copy[k] != distances[k]){
					same = false;
				}
			}
			if(same){
				break;
			}
		}
		//Ici nous sommes censés vérifier qu'il n'y a pas de circuit négatif or il ne peut y en avoir donc il n'y a pas besoin de les vérifier

//		for(int i = 0; i< g.vertices(); i++){
//			System.out.println(parents[i] + " parent de " + i);
//		}
		//On cherche le chemin le plus court qui nous intéresse
		LinkedList<Integer> chemin = new LinkedList<>();
		int fils = t;
		int pere = parents[t];
		chemin.add(fils);
		while(pere != s){
			chemin.add(pere);
			fils = pere;
			pere = parents[fils];
		}
		chemin.add(s);
//		for(Integer i : chemin){
//			System.out.println(i);
//		}

		//Comme le chemin est à l'envers on l'inverse avec un itérateur de LinkedList
		int[] truePath = new int[chemin.size()];
		int n =0;
		Iterator<Integer> ite = chemin.descendingIterator();
		while(ite.hasNext()){
			int i = ite.next();
			truePath[n] = i;
			n++;
		}
//		for(int j = 0; j<truePath.length; j++){
//			System.out.println(truePath[j]);
//		}

		return truePath;
	}
}