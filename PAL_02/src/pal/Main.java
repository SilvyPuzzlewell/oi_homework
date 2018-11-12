package pal;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
	
	//TODO COPIED FROM WEB, CHANGE SO NO RISK OF DUPLICATES 
	static class Reader
    { 
        BufferedReader br; 
        StringTokenizer st;
        
  
        public Reader() throws Exception 
        { 
        	File initialFile = new File("C:\\Users\\user\\Desktop\\oi\\PAL\\DCV02\\datapub\\custon.in");
            br = new BufferedReader(new
                     InputStreamReader(new FileInputStream(initialFile))); 
        } 
        
        String next() throws Exception 
        { 
            while (st == null || !st.hasMoreElements()) 
            { 
                new StringTokenizer(br.readLine());                  
            } 
            return st.nextToken(); 
        } 
        int nextInt() throws Exception, Exception 
        { 
            return Integer.parseInt(next()); 
        }
        
        StringTokenizer nextLine() throws Exception 
        { 
             return new StringTokenizer(br.readLine());     
        }
    } 
	
	public static void addGraphNode(ArrayList<Node> graph, ArrayList<Node> reversedGraph, StringTokenizer line) {
		Integer from = Integer.parseInt(line.nextToken());
		Integer to = Integer.parseInt(line.nextToken());
		
		graph.get(from).addAdjNode(graph.get(to));
		reversedGraph.get(to).addAdjNode(reversedGraph.get(from));
	}
	
	
	public static ArrayList<Node> initGraph(ArrayList<Node> graph, int nVertices) {
		for(int i = 0; i < nVertices; i++) {
			graph.add(new Node(i));
		}
		
		return graph;
	}
		
    public static void main(String[] args) throws Exception {
    	
        int nVertices;
        int nBlueVertices;
        int nEdges;
        HashSet<Integer> blueVertices = new HashSet<>();
        //TODO possibility of adding backward vertices
        ArrayList<Node> graph;
        ArrayList<Node> reversedGraph;
        int end;

        //read from stdInput
        Reader reader = new Reader();
        
        StringTokenizer firstLIne = reader.nextLine();
        nVertices = Integer.parseInt(firstLIne.nextToken());
        nBlueVertices = Integer.parseInt(firstLIne.nextToken());
        nEdges = Integer.parseInt(firstLIne.nextToken());
        graph = initGraph(new ArrayList<Node>(), nVertices);
        reversedGraph = initGraph(new ArrayList<Node>(), nVertices);
        
        //read from file
        StringTokenizer blueVerticesLIne = reader.nextLine();
        for(int i = 0; i < nBlueVertices; i++) {
        	blueVertices.add(Integer.parseInt(blueVerticesLIne.nextToken()));
        }
        for(int i = 0; i < nEdges; i++) {
        	addGraphNode(graph,reversedGraph, reader.nextLine());
        }
                
        DoStuff02 doStuff02 = new DoStuff02();
        doStuff02.nVertices = nVertices;
        doStuff02.nBlueVertices = nBlueVertices;
        doStuff02.nEdges = nEdges;
        doStuff02.blueVertices = blueVertices;
        doStuff02.graph = graph;
        doStuff02.reversedGraph = reversedGraph;
        
        doStuff02.setArrays();
        
        
		long startTime = System.nanoTime();	
        doStuff02.tarjan();
		long estimatedTime = System.nanoTime() - startTime;
        System.out.println("TARJAN SUCC " +  (estimatedTime/1000000));
        
		startTime = System.nanoTime();	
        doStuff02.findAllLongestPaths();
		estimatedTime = System.nanoTime() - startTime;
        System.out.println("Longest Paths SUCC " + (estimatedTime/1000000));
        
		startTime = System.nanoTime();	
        doStuff02.premapBlueVerticesPaths();
		estimatedTime = System.nanoTime() - startTime;
        System.out.println("Blue Vertices SUCC " + (estimatedTime/1000000));
        
        System.out.println("TARJAN " + doStuff02.tarjanCounter + " PATH1 " + doStuff02.longestPathCounter + " PATH2 " + doStuff02.longestPathCounter2);
        
        System.out.println("BLUE1 " + doStuff02.blueCounter + " BLUE2 " + doStuff02.blueCounter2);
        System.out.println("TIME " + (doStuff02.arrayTime/1000000));
        System.out.println(doStuff02.superGlobalMaxLevel + 1);
        
        
        
    }
           
       }


