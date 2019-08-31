/* PrimVsKruskal.java
   CSC 226 - Spring 2019
   Assignment 2 - Prim MST versus Kruskal MST Template
   
   The file includes the "import edu.princeton.cs.algs4.*;" so that yo can use
   any of the code in the algs4.jar file. You should be able to compile your program
   with the command
   
	javac -cp .;algs4.jar PrimVsKruskal.java
	
   To conveniently test the algorithm with a large input, create a text file
   containing a test graphs (in the format described below) and run
   the program with
   
	java -cp .;algs4.jar PrimVsKruskal file.txt
	
   where file.txt is replaced by the name of the text file.
   
   The input consists of a graph (as an adjacency matrix) in the following format:
   
    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>
	
   Entry G[i][j] >= 0.0 of the adjacency matrix gives the weight (as type double) of the edge from 
   vertex i to vertex j (if G[i][j] is 0.0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that G[i][j]
   is always equal to G[j][i].


   R. Little - 03/07/2019
*/

 // package edu.princeton.cs.algs4;
   import edu.princeton.cs.algs4.*;
   import java.util.Scanner;
   import java.io.File;
   import java.util.Arrays;

//Do not change the name of the PrimVsKruskal class
   public class PrimVsKruskal{
   	

	/* PrimVsKruskal(G)
		Given an adjacency matrix for connected graph G, with no self-loops or parallel edges,
		determine if the minimum spanning tree of G found by Prim's algorithm is equal to 
		the minimum spanning tree of G found by Kruskal's algorithm.
		
		If G[i][j] == 0.0, there is no edge between vertex i and vertex j
		If G[i][j] > 0.0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/


		//IMOPORTANT PLEASE READ THE TEXT BELOW!!!
		/* 
		This program compares the mst's of Prims and Kruskals for a graph G. It features an early detectoion
		in that everytime Kruskals inserts an edge, it will check if Prims would insert it. If it's found that 
		Primms would't insert that edge, the program will immediately return a result of false. 
		*/

		static boolean PrimVsKruskal(double[][] G){

		int n = G.length; //the number of vertices in G
		
		boolean pvk = true; //default is true

		pvk = Kruskal(G,n,pvk); //builds the kruskals tree

		return pvk;	//return if they're the same or not
	}


	private static boolean checkprimms(double[][] G, int n, int v, int u){ 
		//checks if the edge inserted from kruskals will be in the primms tree

	   	double[] distances = new double[n]; //the distances
	   	IndexMinPQ<Double> pq = new IndexMinPQ<Double>(n);  //priority queue for the edges
		boolean[] ontree = new boolean[n]; //marks if on tree
		Edge[] edge = new Edge[n]; //the edges on the mst
		Queue<Edge> tmp = new Queue<Edge>(); //tmp queu to hold adjascent nodes
		double[][] primarray = new double[n][n]; //the resultant array representing the mst
		

	   	for(int vertex = 0; vertex<n; vertex++){ //fill the distances with infities
	   		distances[vertex] = Double.POSITIVE_INFINITY; 
	   	}

	   	distances[0] = 0.0; //set initial vertex at 0 distance 
		pq.insert(0, 0.0); //add the first vertex
		int vertex; 

		while(!pq.isEmpty()){
			vertex = pq.delMin();
			ontree[vertex] = true; 

			for(int x = 0; x<n; x++){ //gets the adjascent nodes from the vertex
				if(G[vertex][x]!=0){
					tmp.enqueue(new Edge(vertex,x,G[vertex][x]));
				}
			}

			for(Edge e : tmp){

				int w = e.other(vertex);
				if(ontree[w]) continue;
				if(e.weight() < distances[w]){
					edge[w] = e; //inserts the edge
					distances[w] = e.weight();
					if((e.either() == u || e.other(e.either()) == u) && (e.either() == v || e.other(e.either()) == v)){
						return true; //edge found
					}
					if(pq.contains(w)) pq.change(w,distances[w]);
					else pq.insert(w,distances[w]);
				}

			}
			while(!tmp.isEmpty()){ //clears the queue for the next vertex
				tmp.dequeue();
			}
		}

		return false; //edge was not found, trees are not the same
	}


	private static boolean Kruskal(double[][] G, int n, boolean pvk){

		Queue<Edge> mst = new Queue<Edge>();
		MinPQ<Edge> priorityqueue = new MinPQ<Edge>();
		double[][] kruskalsarray = new double[n][n]; //the array that stores the results for Kruskals

		for(int x = 0; x<n; x++){ //loop that gets all the edges
			for(int y = x; y<n; y++ ){
				if(G[x][y]!=0){
					priorityqueue.insert(new Edge(x,y,G[x][y]));
				}
			}
		}

		UF unionfind = new UF(n);

		while((!priorityqueue.isEmpty()) && (mst.size()< n-1)){
			Edge e = priorityqueue.delMin();
			int v = e.either();
			int w = e.other(v);
			if(unionfind.connected(v,w)){ 
				continue;
			}
			unionfind.union(v,w);
			mst.enqueue(e); //add to the mst
			kruskalsarray[v][w] = G[v][w];
			pvk = checkprimms(G, n, v, w); 
			if(pvk == false){
				break;
			}

		}
		return pvk; 
	}


	/* main()
	   Contains code to test the PrimVsKruskal function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below. 
	*/
	   public static void main(String[] args) {
	   	Scanner s;
	   	if (args.length > 0){
	   		try{
	   			s = new Scanner(new File(args[0]));
	   		} catch(java.io.FileNotFoundException e){
	   			System.out.printf("Unable to open %s\n",args[0]);
	   			return;
	   		}
	   		System.out.printf("Reading input values from %s.\n",args[0]);
	   	}else{
	   		s = new Scanner(System.in);
	   		System.out.printf("Reading input values from stdin.\n");
	   	}

	   	int n = s.nextInt();
	   	double[][] G = new double[n][n];
	   	int valuesRead = 0;
	   	for (int i = 0; i < n && s.hasNextDouble(); i++){
	   		for (int j = 0; j < n && s.hasNextDouble(); j++){
	   			G[i][j] = s.nextDouble();
	   			if (i == j && G[i][j] != 0.0) {
	   				System.out.printf("Adjacency matrix contains self-loops.\n");
	   				return;
	   			}
	   			if (G[i][j] < 0.0) {
	   				System.out.printf("Adjacency matrix contains negative values.\n");
	   				return;
	   			}
	   			if (j < i && G[i][j] != G[j][i]) {
	   				System.out.printf("Adjacency matrix is not symmetric.\n");
	   				return;
	   			}
	   			valuesRead++;
	   		}
	   	}

	   	if (valuesRead < n*n){
	   		System.out.printf("Adjacency matrix for the graph contains too few values.\n");
	   		return;
	   	}	

	   	boolean pvk = PrimVsKruskal(G);
	   	System.out.printf("Does Prim MST = Kruskal MST? %b\n", pvk);
	   }
	}
