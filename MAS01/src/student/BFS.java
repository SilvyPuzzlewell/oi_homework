package student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class BFS {

	public static ArrayList<String> findPathBFS(HashMap<Coordinate, Set<String>> mineMap, Coordinate start, Coordinate goal) 
    { 

        HashMap<Coordinate, Coordinate> visitedMapping = new HashMap<Coordinate, Coordinate>();  
        LinkedList<Coordinate> queue = new LinkedList<Coordinate>(); 
  
        visitedMapping.put(start , null);
        queue.add(start);
        
        Coordinate curNode;
        
        //System.out.println(mineMap.toString());
        //System.out.println(start.toString());
        //System.out
        boolean foundGoal = false;
        while (queue.size() != 0) 
        { 
            curNode = queue.poll();  
            
            Set<String> adj = mineMap.get(curNode);
            if(adj != null && !adj.isEmpty()) {
	            for (String curDirection : adj) {
	            	Coordinate nextCoordinate = mapCoordinate(curDirection, curNode);
	            	if(nextCoordinate.equals(goal)) {
	            		visitedMapping.put(nextCoordinate, curNode);
	            		foundGoal = true;
	            		break;
	            	}
					if(!visitedMapping.containsKey(nextCoordinate)){
						visitedMapping.put(nextCoordinate, curNode);
						queue.add(nextCoordinate);
					}
				}
            }
        }
        
        if(!foundGoal) {
        	return null;
        }
        
        //backtrack, reconvert
        curNode = goal;
        Coordinate parentNode = null;
        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(goal);
        while (true) {
        	parentNode = visitedMapping.get(curNode);
        	if(parentNode == null) break;
        	path.add(parentNode);
        	curNode = parentNode;
        }
        
        ArrayList<String> directionPath = new ArrayList<String>();
        for(int i = path.size() - 1; i > 0; i--) {
        	directionPath.add(convertToString(path.get(i), path.get(i - 1)));
        }
        
        System.out.println(path);
        System.out.println(directionPath);
        return directionPath;
        
    }
	
	public static String convertToString(Coordinate from, Coordinate to){
		int xDiff = to.getX() - from.getX();
		int yDiff = to.getY() - from.getY();
		if(xDiff == 1 && yDiff == 0) {
			return Constants.RIGHT;
		}
		if(xDiff == -1 && yDiff == 0) {
			return Constants.LEFT;
		}
		
		if(xDiff == 0 && yDiff == 1) {
			return Constants.DOWN;
		}

		if(xDiff == 0 && yDiff == -1) {
			return Constants.UP;
		}
		
		System.out.println("SHIT HAPPENED IN CONVERTING");
		System.exit(0);
		return null;
	}

	public static Coordinate mapCoordinate(String direction, Coordinate coordCurrentCoordinate) {
			switch (direction) {
				case Constants.RIGHT:
					return new Coordinate(coordCurrentCoordinate.getX() + 1, coordCurrentCoordinate.getY());
				case Constants.LEFT:
					return new Coordinate(coordCurrentCoordinate.getX() - 1, coordCurrentCoordinate.getY());
				case Constants.UP:
					return new Coordinate(coordCurrentCoordinate.getX(), coordCurrentCoordinate.getY() - 1);
				case Constants.DOWN:
					return new Coordinate(coordCurrentCoordinate.getX(), coordCurrentCoordinate.getY() + 1);
        }
		return null;
    }

}
