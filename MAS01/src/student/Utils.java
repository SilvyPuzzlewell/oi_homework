package student;

import java.util.List;
import java.util.Set;

public class Utils {
	
	//manhattan distances
	public static int computeDistance(Coordinate first, Coordinate second) {				 
		return Math.abs(first.getX() - second.getX()) +  Math.abs(
				first.getY() - second.getY());
	}

	public static int computeDistance(int x, int y, Coordinate second) {
		return 	 Math.abs(x - second.getX()) +  Math.abs( y -  second.getY());
	}
	
	public static Coordinate findNearestGold(Set<Coordinate> goldMap, Coordinate agent) {
		int ret = Integer.MAX_VALUE;
		Coordinate retCoord = null;
		for (Coordinate coordinate : goldMap) {			
			int cur = computeDistance(coordinate, agent);
			if(cur < ret) {
				ret = cur;
				retCoord = coordinate;
			}			
		}		
		return retCoord;
	}
	


}
