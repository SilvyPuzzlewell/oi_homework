package student;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Pathfinding {
    
	
	
	Set<Coordinate> visitedSet = new HashSet<Coordinate>();
	Coordinate goal;
	boolean initialized = false;
	boolean goalReached = false;
	
	int agentId;
	
	
	void init(Coordinate startCoord, Coordinate goal) {
		visitedSet.clear();
		initialized = true;
		this.goal = goal;		
	}
	
	int sign(int value) {
		if(value < 0) {
			return -1;
		} else if(value == 0) {
			return 0;
		} else return 1;
	}
	String getMoveDirection(Coordinate coord, Set<String> openPaths) {
		int xBias = sign(goal.getX() - coord.getX());
		int yBias = sign(goal.getY() - coord.getY());
		
		
		
		int[] xResponse = new int[2];
		if(xBias > 0) {
			xResponse[1] = tryDirectionX(1, Constants.RIGHT, coord, openPaths);
			xResponse[0] = 1;
		} else if(xBias < 0) {
			xResponse[1] = tryDirectionX(1, Constants.LEFT, coord, openPaths);
			xResponse[0] = -1;
		} else {
			xResponse[1] = tryDirectionX(0, Constants.LEFT, coord, openPaths);
			xResponse[0] = -1;
		}
		
		int[] yResponse = new int[2];
		yResponse[0] = yBias;
		if(yBias > 0) {
			yResponse[1] = tryDirectionY(1, Constants.DOWN, coord, openPaths);
			yResponse[0] = 1;
		} else if (yBias < 0) {
			yResponse[1] = tryDirectionY(1, Constants.UP, coord, openPaths);
			yResponse[0] = -1;
		} else {
			yResponse[1] = tryDirectionY(0, Constants.UP, coord, openPaths);
			yResponse[0] = -1;
		}
		
		int xResponse2;
		if(xBias > 0) {
			xResponse2 = tryDirectionX(-1,Constants.LEFT, coord, openPaths);
			if(xResponse2 < xResponse[1]) {xResponse[0] = -1; xResponse[1] = xResponse2;}
		} else if (xBias < 0) {
			xResponse2 = tryDirectionX(-1,Constants.RIGHT, coord, openPaths);
			if(xResponse2 < xResponse[1]) {xResponse[0] = -1; xResponse[1] = xResponse2;}
		} else {
			xResponse2 = tryDirectionX(0, Constants.RIGHT, coord, openPaths);
			if(xResponse2 < xResponse[1]) {xResponse[0] = -1; xResponse[1] = xResponse2;}
		}
		
		int yResponse2;
		if(yBias > 0) {
			yResponse2 = tryDirectionY(-1, Constants.UP, coord, openPaths);
			if(yResponse2 < yResponse[1]) {yResponse[0] = -1; yResponse[1] = yResponse2;}
		} else if (yBias < 0) {
			yResponse2 = tryDirectionY(-1, Constants.DOWN, coord, openPaths);
			if(yResponse2 < yResponse[1]) {yResponse[0] = 1; yResponse[1] = yResponse2;}
		}else {
			yResponse2 = tryDirectionX(0, Constants.DOWN, coord, openPaths);
			if(yResponse2 < yResponse[1]) {yResponse[0] = 1; yResponse[1] = yResponse2;}
		}
		
		System.out.println("Agent " + agentId + " xResponse: 0 - " + xResponse[0] + " 1 - "  + xResponse[1] +
				" yResponse: 0 - " + yResponse[0] + " 1 - "  + yResponse[1] + " xBias " + xBias);
		if(xResponse[1] <= yResponse[1]) {
			if(xResponse[0] == 1){
				Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY());
				visitedSet.add(newCoordinate);
				if(goal.equals(newCoordinate)) {
					goalReached = true;
				}
				return Constants.RIGHT;
			} else {
				Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY());
				visitedSet.add(newCoordinate);
				if(goal.equals(newCoordinate)) {
					goalReached = true;
				}
				return Constants.LEFT;
			}
		} else {
			if(yResponse[0] == -1){
				Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY());
				visitedSet.add(newCoordinate);
				if(goal.equals(newCoordinate)) {
					goalReached = true;
				}
				return Constants.UP;
			} else {
				Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY());
				visitedSet.add(newCoordinate);
				if(goal.equals(newCoordinate)) {
					goalReached = true;
				}
				return Constants.DOWN;
			}			
		}
		
	}
	
	
	private int tryDirectionX(int xBias, String direction, Coordinate coord, Set<String> openPaths) {
		int shift = 0;
		if(direction.equals(Constants.RIGHT)) {
			shift = 1;
		} else shift = -1;
		
		if(!openPaths.contains(direction)) {
			return 7;
		}
		if(!visitedSet.contains(new Coordinate(coord.getX() + shift, coord.getY()))) {
			if(xBias == 1) {
				return 1;
			} else if(xBias == 0) {
				return 2;
			} else {
				return 3;
			}
			
		}		
		if(xBias == 1) {
			return 4;
		} else if(xBias == 0) {
			return 5;
		} else {
			return 6;
		}
	}
	
	private int tryDirectionY(int yBias, String direction, Coordinate coord, Set<String> openPaths) {
		int shift = 0;
		if(direction.equals(Constants.DOWN)) {
			shift = 1;
		} else shift = -1;
		
		if(!openPaths.contains(direction)) {
			return 7;
		}
		if(!visitedSet.contains(new Coordinate(coord.getX(), coord.getY() + shift))) {
			if(yBias == 1) {
				return 1;
			} else if(yBias == 0) {
				return 2;
			} else {
				return 3;
			}
			
		}		
		if(yBias == 1) {
			return 4;
		} else if(yBias == 0) {
			return 5;
		} else {
			return 6;
		}
	}
	
	
	
}
