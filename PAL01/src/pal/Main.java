package pal;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
		
    public static void main(String[] args) throws IOException {
    	
        int nVertices;
        int nEdges;
        int start;
        int end;

        //read from stdInput
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
        
        //read from file
        
        
        String filename = "C:\\Users\\user\\Desktop\\oi\\PAL\\DCV01\\data\\pub09.in";
        FileReader fr = new FileReader(filename);
        bi = new BufferedReader(fr);
        
        
        StringTokenizer stk = new StringTokenizer(bi.readLine());
        DoStuff01 dostuff = new DoStuff01();
        
        nVertices = Integer.parseInt(stk.nextToken());
        nEdges = Integer.parseInt(stk.nextToken());
        start = Integer.parseInt(stk.nextToken());
        end = Integer.parseInt(stk.nextToken());
        dostuff.end = end;
        dostuff.numberOfVertices = nVertices;
        dostuff.beginning = start;
       
		long startTime = System.nanoTime();			
		 for (int i = 0; i < nEdges; i++) {
        	//add to adjacency list
        	StringTokenizer stk2 = new StringTokenizer(bi.readLine());
        	dostuff.addGraphNode(Integer.parseInt(stk2.nextToken()),Integer.parseInt(stk2.nextToken()),Integer.parseInt(stk2.nextToken()));
        }
		 long estimatedTime = System.nanoTime() - startTime;		
		 System.out.println("data load time: " + estimatedTime);
	    
		 
		startTime = System.nanoTime();	 
        dostuff.BFS(start, end);
		estimatedTime = System.nanoTime() - startTime;		
		System.out.println("program time: " + estimatedTime);
        
        //debug, do not upload
        if (bi != null)
			bi.close();
        /*
		if (fr != null)
			fr.close();
			*/
    	
    }
           
       }


