package student;

import mas.agents.AbstractAgent;
import mas.agents.Message;
import mas.agents.SimulationApi;
import mas.agents.StringMessage;
import mas.agents.task.mining.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Agent extends AbstractAgent {
	public Agent(int id, InputStream is, OutputStream os, SimulationApi api) throws IOException, InterruptedException {
		super(id, is, os, api);
	}

	// See also class StatusMessage
	public static String[] types = { "", "obstacle", "depot", "gold", "agent" };

	// message strings
	final int NUMBER_OF_MINERS = 4;
	final String HELP_REQUEST_MESSAGE = "helpme";
	final String DEPOT_FOUND_MESSAGE = "lol ifoundthedepot";

	// state strings
	final String ON_THE_WAY_TO_HELP_PICKING_UP = "ON_THE_WAY_TO_HELP_PICKING_UP";
	final String WAITING_FOR_PICKUP_HELP = "WAITING_FOR_PICKUP_HELP";
	final String WAITING_FOR_PICKUP_HELP_CONFIRMATION_MESSAGE = "WAITING_FOR_PICKUP_HELP_CONFIRMATION_MESSAGE";
	final String WAITING_FOR_AGENT_TO_PICK_UP_GOLD = "WAITING_FOR_AGENT_TO_PICK_UP_GOLD";
	final String GOLD_PICKUP = "GOLD_PICKUP";
	final String GOING_TO_DEPO = "GOING_TO_DEPO";
	final String RANDOM_DEPO_SEARCH_WITH_GOLD = "RANDOM_DEPO_SEARCH_WITH_GOLD";
	final String FREE = "FREE";
	final String TRYING_TO_FIND_FREE_HELPER = "TRYING_TO_FIND_FREE_HELPER";
	final String GOING_TO_PICK_UP_GOLD = "GOLD_SEARCH";

	// runtime globals
	Coordinate depotPosition;
	Coordinate goldPickingHelper; // must be not null after request for help was send
	Coordinate goldPicker; // must not be null after accepting help request
	int goldPickerId;
	int goldPickerHelperId;
	int tryingToFindFreeHelperTries = 0;
	ArrayList<String> pathDirections = new ArrayList<>();
	int cursor = 0;
	String agentState = FREE;
	int stuckCounter = 0;
	final int STUCK_THRESHOLD = 2000;
	Coordinate bfsGoal;
	
	final long agent1SleepTime = 0;
	final long agent2SleepTime = 1;
	final long agent3SleepTime = 2;
	final long agent4SleepTime = 3;
	
	

	// information about agent current position
	StatusMessage agentStatus;
	Coordinate agentCoordinate;
	Set<String> openPaths = new HashSet<String>();
	boolean goldFound;

	// shared structures
	HashMap<Integer, Coordinate> otherAgentsCoordinates = new HashMap<Integer, Coordinate>();
	HashMap<Integer, String> otherAgentsBusyState = new HashMap<Integer, String>();
	HashMap<Coordinate, Set<String>> mineMap = new HashMap<Coordinate, Set<String>>();
	HashSet<Coordinate> goldMap = new HashSet<Coordinate>();
	Pathfinding pathfinding = new Pathfinding();
	Movement movement = new Movement();

	// parameters
	final int TRYING_TO_FIND_FREE_HELPER_MAXIMUM_ALLOWED_TRIES = 5;

	// returns index of miner closest to the current one
	public int findNearestMiner(int thisAgentX, int thisAgentY) {
		double curMinDistance = Double.MAX_VALUE;
		int miner = -1;
		for (int i = 0; i < NUMBER_OF_MINERS; i++) {
			if (i == this.getAgentId())
				continue;
			double curDistance = Utils.computeDistance(thisAgentX, thisAgentY, otherAgentsCoordinates.get(i));
			if (curDistance < curMinDistance) {
				curMinDistance = curDistance;
				miner = i;
			}
		}
		return miner;
	}

	public int findNearestFreeMiner(int thisAgentX, int thisAgentY) {
		double curMinDistance = Double.MAX_VALUE;
		int miner = -1;
		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i == this.getAgentId() || !isFree(i))
				continue;
			double curDistance = Utils.computeDistance(thisAgentX, thisAgentY, otherAgentsCoordinates.get(i));
			if (curDistance < curMinDistance) {
				curMinDistance = curDistance;
				miner = i;
			}
		}
		return miner;
	}

	public boolean isInNeighborhood(int thisAgentX, int thisAgentY, Coordinate otherAgent) {
		int xDiff = otherAgent.getX() - thisAgentX;
		int yDiff = otherAgent.getY() - thisAgentY;

		if ((Math.abs(xDiff) == 1 && Math.abs(yDiff) == 0) || (Math.abs(xDiff) == 0 && Math.abs(yDiff) == 1)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * public int getSomeAgentInNeighborhood(int thisAgentX, int thisAgentY) { for
	 * (Entry<Integer, Coordinate> entry : otherAgentsCoordinates.entrySet()) {
	 * if(isInNeighborhood(thisAgentX, thisAgentY, entry.getKey())){ return
	 * entry.getKey(); } } return -1; }
	 */

	public void addObstaclePosition(StatusMessage.SensorData data) throws Exception {

		if (data.x == agentStatus.agentX + 1 && data.y == agentStatus.agentY) {
			// log("RIGHT removed");
			openPaths.remove(Constants.RIGHT);
		}
		if (data.x == agentStatus.agentX - 1 && data.y == agentStatus.agentY) {
			// log("LEFT removed");
			openPaths.remove(Constants.LEFT);
		}
		if (data.x == agentStatus.agentX && data.y + 1 == agentStatus.agentY) {
			// log("DOWN removed");
			openPaths.remove(Constants.UP);
		}
		if (data.x == agentStatus.agentX && data.y - 1 == agentStatus.agentY) {
			// log("UP removed");
			openPaths.remove(Constants.DOWN);
		}
		;
	}

	public void addMapLimits() throws Exception {
		// log("Border status x: " + status.width + " " + status.agentX+1);
		// log("Border status:y" + status.width + " " + status.agentX+1);
		if (agentStatus.width == agentStatus.agentX + 1) {
			openPaths.remove(Constants.RIGHT);
		}
		if (agentStatus.agentX == 0) {
			openPaths.remove(Constants.LEFT);
		}
		if (agentStatus.height == agentStatus.agentY + 1) {
			openPaths.remove(Constants.DOWN);
		}
		if (agentStatus.agentY == 0) {
			openPaths.remove(Constants.UP);
		}
	}
	// TODO anti-congestion

	public void mapObstaclesFromSensorData() throws Exception {
		openPaths = new HashSet<String>(Arrays.asList(Constants.RIGHT, Constants.LEFT, Constants.UP, Constants.DOWN));
		addMapLimits();
		// mapOtherAgents(currentAgentCoordinate);
		for (StatusMessage.SensorData data : agentStatus.sensorInput) {
			if (data.type == 1) {
				addObstaclePosition(data);
			}
		}
	}

	// antiCongestionProcedure, works only heuristically

	Set<String> mapOtherAgents() {

		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i != this.getId()) {
				int xDiff = otherAgentsCoordinates.get(i).getX() - agentCoordinate.getX();
				int yDiff = otherAgentsCoordinates.get(i).getY() - agentCoordinate.getY();
				if (xDiff == 1 && yDiff == 0) {
					openPaths.remove(Constants.RIGHT);
				}
				if (xDiff == -1 && yDiff == 0) {
					openPaths.remove(Constants.LEFT);
				}

				if (xDiff == 0 && yDiff == 1) {
					openPaths.remove(Constants.DOWN);
				}

				if (xDiff == 0 && yDiff == -1) {
					openPaths.remove(Constants.UP);
				}
			}
		}

		return openPaths;
	}

	public void sendDepotCoordinates(Coordinate depoCoordinate) throws IOException {
		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i != this.getId()) {
				sendMessage(i, new DepotCoordinatesMessage(depoCoordinate));
			}
		}
		depotPosition = depoCoordinate;
	}

	public void initPathfinding(Coordinate goal) throws Exception {
		StatusMessage status = sense();
		pathfinding.init(new Coordinate(status.agentX, status.agentY), goal);
	}

	public boolean isFree(int agent) {
		if (otherAgentsBusyState.get(agent).equals(FREE)) {
			return true;
		} else
			return false;
	}

	public void stepToPoint() throws Exception {
		// TODO
		// String direction = pathDirections.get(pathDirectionsCursor);
		// String move = pathfinding.getMoveDirection(new Coordinate(status.agentX,
		// status.agentY), openPaths);

		bfsMove();
	}

	private void bfsMove() throws Exception {
		if(cursor >= pathDirections.size()) {
			resetBfs();
			return;
		}
		boolean move = movement.move(agentCoordinate, pathDirections.get(cursor));
		if (move) {
			cursor++;
			stuckCounter = 0;
		} else {
			stuckCounter++;
//			log("RECHARGE " + stuckCounter);
			if (stuckCounter >= STUCK_THRESHOLD) {				
				antiCombustionMove();
				resetBfs();
				
			}
		}
	}
	
	void resetBfs() throws Exception {
		boolean newPathFound = false;
		do {
			initBfsSearch();
			pathDirections = BFS.findPathBFS(mineMap, agentCoordinate, bfsGoal);
			if (pathDirections == null) {
				boolean moved = false;
				do {
					moved = movement.move(agentCoordinate, movement.getRandomDirection(openPaths));
				} while (!moved);
			}
			if (pathDirections != null) {
				newPathFound = true;
			}
		} while (!newPathFound);
		
	cursor = 0;
	stuckCounter = 0;
	}
	
	void antiCombustionMove() throws Exception {
		int moveCounter = 0;
		
		if(this.getAgentId() == 1)
		do {
			if(movement.move(agentCoordinate, movement.getRandomDirection(openPaths)))
				{moveCounter++;}
		} while (moveCounter < agent1SleepTime);
		
		if(this.getAgentId() == 2)
		do {
			if(movement.move(agentCoordinate, movement.getRandomDirection(openPaths)))
				{moveCounter++;}
		} while (moveCounter < agent2SleepTime);
		
		if(this.getAgentId() == 3)
		do {
			if(movement.move(agentCoordinate, movement.getRandomDirection(openPaths)))
				{moveCounter++;}
		} while (moveCounter < agent3SleepTime);
		
		if(this.getAgentId() == 4)
		do {
			if(movement.move(agentCoordinate, movement.getRandomDirection(openPaths)))
				{moveCounter++;}
		} while (moveCounter < agent4SleepTime);
	}
	

	/*
	 * if (pathfinding.goalReached) { sendConfirmationMessage(); }
	 */

	public void logOpenPaths(Set<String> openPaths) throws Exception {
		StringBuilder logStr = new StringBuilder();
		for (String path : openPaths) {
			logStr.append(path + " ");
		}
		log(logStr.toString());
	}

	// first
	public void actWhenFree() throws Exception {

		if (agentStatus.isAtDepot() && depotPosition == null) {
			sendDepotCoordinates(agentCoordinate);
		}

		mapObstaclesFromSensorData();
		// log("BEFORE: " + agentStatus.agentX + " " + agentStatus.agentY);
		movement.moveDFS(agentCoordinate, openPaths);
		// log("AFTER: " + agentStatus.agentX + " " + agentStatus.agentY);

		if (!goldMap.isEmpty() && depotPosition != null && (this.getAgentId() == 1 || this.getAgentId() == 2)) {
			switchToGoingToPickupGoldState();
		}

		// old version
		/*
		 * if (agentStatus.isAtGold() && depotPosition != null) {
		 * if(!sendPickupHelpRequest(agentCoordinate)) { agentState =
		 * TRYING_TO_FIND_FREE_HELPER; return; } agentState = WAITING_FOR_PICKUP_HELP;
		 * return; }
		 */
	}

	// now agent must be put to sleep state and wait for response of the called one.
	public void sleepTurn() {

	}

	public void stepToPointToGolddigger() throws Exception {
		if (isInNeighborhood(agentCoordinate.getX(), agentCoordinate.getY(), goldPicker)) {
			agentState = WAITING_FOR_AGENT_TO_PICK_UP_GOLD;
			log("SENDING SAFE TO PICK UP MESSAGE " + " my x: " + agentCoordinate.getX() + " my y: "
					+ agentCoordinate.getY() + " goldpicker x " + goldPicker.getX() + " goldpicker y "
					+ goldPicker.getY());
			sendMessage(goldPickerId, new ConfirmInNeighborhoodMessage());
			return;
		}
		stepToPoint();

	}

	// agent is now safe to pickup the goddamned gold
	public void pickUpGold(int helpingAgentNumber) throws Exception {
		// log("PICKING GOLD UP");
		if (!goldMap.contains(bfsGoal)) {
			agentState = FREE;
			return;
		}
		agentStatus = pick();
		if (!agentStatus.isAtGold()) {
			goldMap.remove(agentCoordinate);
		}
		sendMessage(helpingAgentNumber, new GoldPickedMessage());
		if (depotPosition == null) {
			agentState = RANDOM_DEPO_SEARCH_WITH_GOLD;
		} else {
			switchToGoingToDepoState();
		}
	}

	public void stepToPointDepoSearch() throws Exception {
		if (agentStatus.isAtDepot()) {
			drop();
			agentState = FREE;
			return;
		}
		stepToPoint();

	}

	public void randomDepoSearchWithGold() throws Exception {
		if (agentStatus.isAtDepot() && depotPosition == null) {
			sendDepotCoordinates(agentCoordinate);
			agentState = FREE;
			drop();
			return;
		}

		movement.moveDFS(agentCoordinate, openPaths);
	}

	public void tryingToFindFreeHelperStateAction() throws Exception {
		if (!sendPickupHelpRequest(agentCoordinate)) {
			tryingToFindFreeHelperTries++;
		}

		if (tryingToFindFreeHelperTries == TRYING_TO_FIND_FREE_HELPER_MAXIMUM_ALLOWED_TRIES) {
			agentState = FREE;
			tryingToFindFreeHelperTries = 0;
			movement.moveDFS(agentCoordinate, openPaths);
		}
	}

	// ---Gold searching states
	public void actInGoingToPickupGoldState() throws Exception {
		if (!goldMap.contains(bfsGoal)) {
			agentState = FREE;
			return;
		}
		stepToPoint();
		// TODO call agent when going for gold
		if (agentStatus.isAtGold()) {
			if (!sendPickupHelpRequest(agentCoordinate)) {
				agentState = TRYING_TO_FIND_FREE_HELPER;
				return;
			}
			agentState = WAITING_FOR_PICKUP_HELP;
			return;
		}
	}

	// ---

	@Override
	public void act() throws Exception {
		sendMessage(1, new StringMessage("Hello"));
		// System.out.println(this.getAgentId());
		initOtherAgents();
		pathfinding.agentId = this.getAgentId();
		movement.agentId = this.getAgentId();
		movement.agent = this;

		int round = 0;

		while (true) {
			agentStatus = sense();
			agentCoordinate = new Coordinate(agentStatus.agentX, agentStatus.agentY);
			mapObstaclesFromSensorData();
			goldFound = false;

			while (messageAvailable()) {

				Message m = readMessage();

				// info about other agents
				if (m instanceof AgentStatusMessage) {
					otherAgentsCoordinates.put(m.getSender(), ((AgentStatusMessage) m).getCoordinate());
					otherAgentsBusyState.put(m.getSender(), ((AgentStatusMessage) m).getAgentStatus());
					if (!mineMap.containsKey(((AgentStatusMessage) m).getCoordinate())) {
						// log("Got minemap coordinate!!!");
						mineMap.put(((AgentStatusMessage) m).getCoordinate(), ((AgentStatusMessage) m).getOpenPaths());
					}
					if (((AgentStatusMessage) m).isGoldFound()
							&& !goldMap.contains(((AgentStatusMessage) m).getCoordinate())) {
						goldMap.add(((AgentStatusMessage) m).getCoordinate());
					}
				}

				if (m instanceof PickupHelpRequestMessage) {
					handlePickupHelpRequest((PickupHelpRequestMessage) m);
				}
				//
				if (m instanceof ConfirmFreeMessage) {
					if (!((ConfirmFreeMessage) m).isFree()) {
						agentState = FREE;
						// log("Rogered, trying to find someone else mate, " + m.getSender());
						/*
						 * if(!sendPickupHelpRequest(currentAgentCoordinate)) { agentStatus =
						 * TRYING_TO_FIND_FREE_HELPER; };
						 */
					}
				}

				if (m instanceof ConfirmInNeighborhoodMessage) {
					agentState = GOLD_PICKUP;
				}

				if (m instanceof GoldPickedMessage) {
					agentState = FREE;
				}

				if (m instanceof DepotCoordinatesMessage) {
					depotPosition = ((DepotCoordinatesMessage) m).getCoordinate();
					if (agentState.equals(RANDOM_DEPO_SEARCH_WITH_GOLD)) {
						switchToGoingToDepoState();
					}
				}
			}

			// state/action switch
			if (agentState.equals(FREE)) {
				actWhenFree();
			} else if (agentState.equals(WAITING_FOR_PICKUP_HELP)) {
				sleepTurn();
			} else if (agentState.equals(GOLD_PICKUP)) {
				pickUpGold(goldPickerHelperId);
			} else if (agentState.equals(RANDOM_DEPO_SEARCH_WITH_GOLD)) {
				randomDepoSearchWithGold();
			} else if (agentState.equals(GOING_TO_DEPO)) {
				stepToPointDepoSearch();
			} else if (agentState.equals(ON_THE_WAY_TO_HELP_PICKING_UP)) {
				stepToPointToGolddigger();
			} else if (agentState.equals(WAITING_FOR_AGENT_TO_PICK_UP_GOLD)) {
				sleepTurn();
			} else if (agentState.equals(TRYING_TO_FIND_FREE_HELPER)) {
				tryingToFindFreeHelperStateAction();
			} else if (agentState.equals(GOING_TO_PICK_UP_GOLD)) {
				actInGoingToPickupGoldState();
			}

			// some garbage
			/*
			 * for (StatusMessage.SensorData data : status.sensorInput) { /* if(data.type ==
			 * 3 && data.x == status.agentX && data.y == status.agentY){ pick(); }
			 */
			// log(String.format("got gold!!!"));

			// }
			/*
			 * log(String.format("Round: " + round + " ; am now on position " +
			 * status.agentX + " " + status.agentY)); printOtherCoordinates();
			 * 
			 * for (StatusMessage.SensorData data : status.sensorInput) { //
			 * log(String.format("I see %s at [%d,%d]", types[data.type], data.x, data.y));
			 * }
			 * 
			 * // REMOVE THIS BEFORE SUBMITTING YOUR SOLUTION TO BRUTE !! // (this is meant
			 * just to slow down the execution a bit for demonstration // purposes)
			 * 
			 */
			/*
			 * 
			 * 
			 * 
			 * printOtherCoordinates();
			 */

			// printOtherCoordinates();
			//log(String.format("Round: " + round + " ; am now on position " +
			//status.agentX + " " + status.agentY + " in status " + agentStatus + "
			// mineMapSize " + mineMap.size() ));

			
			  //try { Thread.sleep(200); } catch (InterruptedException ie) { }
			 

			//log(String.format("Round: " + round + " ; am now on position " +
			//agentCoordinate.getX() + " " + agentCoordinate.getY() + " in status " +
			// agentState + " mineMapSize " + mineMap.size() ));
			// System.out.println(goldMap);
			sendStatus();
			round++;
		}
	}

	public void handlePickupHelpRequest(PickupHelpRequestMessage m) throws Exception {
		initBfsSearch();
		pathDirections = BFS.findPathBFS(mineMap, agentCoordinate, m.getSenderCoordinate());
		// path not yet found
		// is not free or path towards sender is not yet discovered
		if (!agentState.equals(FREE) || pathDirections == null) {
			// log("I'm not free buddy, " + m.getSender());
			sendMessage(m.getSender(), new ConfirmFreeMessage(false));
		} else {
			bfsGoal = m.getSenderCoordinate();
			switchToOnTheWayHelpingPickingUpState(m, agentCoordinate);
		}
	}

	public boolean sendPickupHelpRequest(Coordinate senderCoordinate) throws Exception {
		goldPickerHelperId = findNearestFreeMiner(senderCoordinate.getX(), senderCoordinate.getY());
		// log("PLS HELP friend, "+ goldPickingHelper);
		if (goldPickerHelperId == -1)
			return false;
		sendMessage(goldPickerHelperId, new PickupHelpRequestMessage(senderCoordinate));
		return true;
	}

	private void switchToGoingToDepoState() {
		initBfsSearch();
		pathDirections = BFS.findPathBFS(mineMap, agentCoordinate, depotPosition);
		// path not yet found
		if (pathDirections == null) {
			return;
		}
		bfsGoal = depotPosition;
		agentState = GOING_TO_DEPO;
	}

	private void switchToGoingToPickupGoldState() {
		initBfsSearch();
		Coordinate nearestGold = Utils.findNearestGold(goldMap, agentCoordinate);
		pathDirections = BFS.findPathBFS(mineMap, agentCoordinate, nearestGold);
		if (pathDirections == null) {
			return;
		}
		bfsGoal = nearestGold;
		agentState = GOING_TO_PICK_UP_GOLD;
	}

	private void initBfsSearch() {
		if (pathDirections != null && !pathDirections.isEmpty()) {
			pathDirections.clear();
		}
		mineMap.put(agentCoordinate, openPaths);
		cursor = 0;
	}

	private void switchToOnTheWayHelpingPickingUpState(PickupHelpRequestMessage m, Coordinate helperCoordinate) {
		goldPickerId = m.getSender();
		goldPicker = m.getSenderCoordinate();
		agentState = ON_THE_WAY_TO_HELP_PICKING_UP;
	}

	// Message Sending methods
	public void sendStatus() throws IOException {
		mineMap.put(agentCoordinate, openPaths);
		if (goldFound) {
			goldMap.add(agentCoordinate);
		}
		;
		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i != this.getId()) {
				sendMessage(i, new AgentStatusMessage(agentCoordinate, agentState, openPaths, goldFound));
			}
		}
	}

	public void printOtherCoordinates() throws Exception {
		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i != this.getId()) {
				Coordinate cur = otherAgentsCoordinates.get(i);
				log(String.format("Agent " + i + ": x " + cur.getX() + " y: " + cur.getY() + " status: "
						+ otherAgentsBusyState.get(i)));
			}
		}
	}

	public void initOtherAgents() {
		for (int i = 1; i <= NUMBER_OF_MINERS; i++) {
			if (i != this.getId()) {
				otherAgentsCoordinates.put(i, new Coordinate(0, 0));
				otherAgentsBusyState.put(i, "UNITIALIZED");
			}
		}
	}
}
