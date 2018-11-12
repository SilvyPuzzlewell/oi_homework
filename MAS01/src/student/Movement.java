package student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import mas.agents.task.mining.StatusMessage;

public class Movement {
	// for backtracking reasons
	Map<Coordinate, Coordinate> visitedSet = new HashMap<Coordinate, Coordinate>();
	Stack<Coordinate> stack = new Stack<Coordinate>();
	int agentId;
	Agent agent;
	
	int stuckCounter = 0;
	Coordinate previousCoordinate;
	
	final int STUCK_COUNTER_THRESHOLD = 3;

	public void moveDFS(Coordinate coord, Set<String> openPaths) throws Exception {
		int newOnStack = 0;
		if (agentId == 1) {
			Coordinate newCoordinate = new Coordinate(coord.getX() + 1, coord.getY());
			if (openPaths.contains(Constants.RIGHT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}
			newCoordinate = new Coordinate(coord.getX() - 1, coord.getY());
			if (openPaths.contains(Constants.LEFT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() + 1);
			if (openPaths.contains(Constants.DOWN) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() - 1);
			if (openPaths.contains(Constants.UP) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}
			//System.out.println(stack);
			moveDFSinternal(coord, newOnStack);
			
			

		} 
		//else if (agentId == 2 || agentId == 3 || agentId == 4) {
		//	return;
		//}
		
		//else if (agentId == 2 || agentId == 3) {
		//return;
		//}


		if (agentId == 2) {
			Coordinate newCoordinate = new Coordinate(coord.getX() - 1, coord.getY());
			if (openPaths.contains(Constants.LEFT) && !visitedSet.containsKey(newCoordinate)) {				
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX() + 1, coord.getY());
			if (openPaths.contains(Constants.RIGHT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() - 1);
			if (openPaths.contains(Constants.UP) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() + 1);
			if (openPaths.contains(Constants.DOWN) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}
			
			moveDFSinternal(coord, newOnStack);
		}

		if (agentId == 3) {

			Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY() - 1);
			if (openPaths.contains(Constants.UP) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() + 1);
			if (openPaths.contains(Constants.DOWN) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
				
			}

			newCoordinate = new Coordinate(coord.getX() - 1, coord.getY());
			if (openPaths.contains(Constants.LEFT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX() + 1, coord.getY());
			if (openPaths.contains(Constants.RIGHT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}
			
			moveDFSinternal(coord, newOnStack);
		}

		if (agentId == 4) {
			Coordinate newCoordinate = new Coordinate(coord.getX(), coord.getY() - 1);
			if (openPaths.contains(Constants.UP) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX(), coord.getY() + 1);
			if (openPaths.contains(Constants.DOWN) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX() - 1, coord.getY());
			if (openPaths.contains(Constants.LEFT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}

			newCoordinate = new Coordinate(coord.getX() + 1, coord.getY());
			if (openPaths.contains(Constants.RIGHT) && !visitedSet.containsKey(newCoordinate)) {
				stack.push(newCoordinate);
				newOnStack++;
			}
			
			moveDFSinternal(coord, newOnStack);
		}

	}
	
	private void moveDFSinternal(Coordinate coord, int newOnStack) throws Exception {
		Coordinate old = new Coordinate(coord.getX(), coord.getY());
		if (newOnStack > 0) {
			
			Coordinate movementTo = stack.pop();
			String instruction = convertToString(coord, movementTo);

			
			// move was succesful
			if (move(coord, instruction)) {
				visitedSet.put(movementTo, old);
				printCoordIfMoveNotValid(movementTo, old, 0, "");
			} // move was not succesful, way is blocked by other agent, try different
			else {
				// (too fugin convoluted) backup method searching other found passable ways, if
				// one fits, agent moves that way and the rest is added to back on stack
				agent.log("TODO TROUBLE STACK1");
//				System.out.println(stack);
				visitedSet.put(movementTo, coord); // agent was here, therefore it doesn't need exploration
				printCoordIfMoveNotValid(movementTo, old, 1, "");
				boolean success = false;
				for (int i = 0; i < newOnStack - 1; i++) {
					Coordinate coordinateAlternativeMovement = stack.pop();
					instruction = convertToString(coord, coordinateAlternativeMovement);
					if (move(coord, instruction)) {
						visitedSet.put(movementTo, coord);
						printCoordIfMoveNotValid(movementTo, old, 2, newOnStack + " " + i);
						success = true;
						break;
					} else {
						visitedSet.put(movementTo, coord);
						printCoordIfMoveNotValid(movementTo, old, 3, "");
					}
					
				}
				
				//no passable way, moving back
				instruction = convertToString(coord, visitedSet.get(coord));
				if (instruction != null) {
					 move(coord, instruction);
				} else {
					agent.log("TODO TROUBLE STACK2");
				}
			}

		} else {
			// return to parent, we also need to throw away all new st
			// needed because it's not a valid move position for now
			String instruction = convertToString(coord, visitedSet.get(coord));
			if (instruction != null) {
				move(coord, instruction);
			} else {
	//			agent.log("TODO TROUBLE STACK3: x: " + coord.getX() + " " +
	//					visitedSet.get(coord).getX() + " y: " + coord.getY() +" "+ visitedSet.get(coord).getY());
				agent.log("STCK3");
			}
		}
		//anti-stuck measure
		
		if(previousCoordinate != null && coord.equals(previousCoordinate)) {
			stuckCounter++;
			agent.log("Counter " + stuckCounter);
			if(stuckCounter == STUCK_COUNTER_THRESHOLD) {
				stack.clear();
				visitedSet.clear();
				stuckCounter = 0;
			}
		}else {
			stuckCounter = 0;
		}
		
		previousCoordinate = old;
	}

	public boolean move(Coordinate originalCoordinate, String direction) throws Exception {
		if (direction != null) {
		//	agent.log(direction);
		}
		//agent.logOpenPaths(agent.openPaths);
		StatusMessage status = null;
		//agent.log("BEFORE: " + agent.sense().agentX + " " + agent.sense().agentY);
		//agent.log(direction);
		if (direction != null) {
			switch (direction) {
			case Constants.RIGHT:
				status = agent.right();
				break;
			case Constants.LEFT:
				status = agent.left();
				break;
			case Constants.UP:
				status = agent.up();
				break;
			case Constants.DOWN:
				status = agent.down();
				break;
			default:
			}
		}
		//agent.log("AFTER: " + status.agentX + " " + status.agentY);
		//TODO check for successful move - prevent recomputing obstacles
		if(status != null && isMoveValid(originalCoordinate, status)) {		
			agent.agentStatus = status;
			agent.agentCoordinate.setX(status.agentX);
			agent.agentCoordinate.setY(status.agentY);
			agent.mapObstaclesFromSensorData();
			if(status.isAtGold()) {
				agent.goldFound = true;
			}
			return true;
		}
		return false;
	}
	
	public boolean isMoveValid(Coordinate coord, StatusMessage status) {
		return coord.getX() != status.agentX || coord.getY() != status.agentY;
	}
	
	public void printCoordIfMoveNotValid(Coordinate one, Coordinate two, int identifier, String additionalMsgs) throws Exception {
		int xDiff = one.getX() - two.getX();
		int yDiff = one.getY() - two.getY();

		if (!((Math.abs(xDiff) == 1 && Math.abs(yDiff) == 0) || (Math.abs(xDiff) == 0 && Math.abs(yDiff) == 1))) {
			System.err.println("NOT VALID MOVE in " + identifier +" coordinates: " + one.toString() + " " + two.toString() + " msg: " + 
		additionalMsgs);
			System.err.println(stack);
		} 
	}
	
	

	public String convertToString(Coordinate from, Coordinate to) {
		if(from == null || to == null) {
			return null;
		}
		int xDiff = to.getX() - from.getX();
		int yDiff = to.getY() - from.getY();
		if (xDiff == 1 && yDiff == 0) {
			return Constants.RIGHT;
		}
		if (xDiff == -1 && yDiff == 0) {
			return Constants.LEFT;
		}

		if (xDiff == 0 && yDiff == 1) {
			return Constants.DOWN;
		}

		if (xDiff == 0 && yDiff == -1) {
			return Constants.UP;
		}
		return null;
	}

	public String getRandomDirection(Set<String> openPaths) {
		// Set<String> openPaths = mapObstaclesFromSensorData(status);
		if (openPaths.size() == 0) {
			return null;
		}
		int randomNum = ThreadLocalRandom.current().nextInt(0, openPaths.size());
		// log(randomNum);
		int cur = 0;
		for (String path : openPaths) {
			if (cur == randomNum) {
				return path;
			}
			cur++;
		}

		// should never fucking happen
		return null;
	}
}
