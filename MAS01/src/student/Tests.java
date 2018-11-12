package student;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tests {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pathfinding path = new Pathfinding();
		path.agentId = 1;
		Coordinate goal =  new Coordinate(10, 10);
		path.init(new Coordinate(5,5), new Coordinate(10, 10));
		
		Set<String> openPaths = new HashSet<String>(Arrays.asList(Constants.RIGHT, Constants.LEFT));
		
		System.out.println(path.getMoveDirection(new Coordinate (6, 4), openPaths)); //goes to right
		openPaths = new HashSet<String>(Arrays.asList(Constants.LEFT));
		System.out.println(path.getMoveDirection(new Coordinate (7, 4), openPaths));
		openPaths = new HashSet<String>(Arrays.asList(Constants.RIGHT, Constants.LEFT));
		System.out.println(path.getMoveDirection(new Coordinate (6, 4), openPaths));
		
		Coordinate curCoordinate = new Coordinate(7, 4);
		
		
		openPaths = new HashSet<String>(Arrays.asList(Constants.RIGHT, Constants.LEFT, Constants.UP, Constants.DOWN));
		while(!curCoordinate.equals(goal)) {
			String direction = path.getMoveDirection(curCoordinate, openPaths);
			System.out.println(direction + " " +curCoordinate.getX() + " " + curCoordinate.getY());
			switch (direction) {
				case Constants.RIGHT:
					curCoordinate.setX(curCoordinate.getX() + 1);
					break;
				case Constants.LEFT:
					curCoordinate.setX(curCoordinate.getX() - 1);
					break;
				case Constants.UP:
					curCoordinate.setY(curCoordinate.getY() - 1);
					break;
				case Constants.DOWN:
					curCoordinate.setY(curCoordinate.getY() + 1);
					break;
			}
		}
		
		System.out.println(path.getMoveDirection(new Coordinate (10, 12), openPaths));
		
		
		
		
		//openPaths.remove(Constants.RIGHT);
		//System.out.println(path.getMoveDirection(new Coordinate (6, 4), openPaths));
	}

}
