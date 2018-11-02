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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Agent extends AbstractAgent {
    public Agent(int id, InputStream is, OutputStream os, SimulationApi api) throws IOException, InterruptedException {
        super(id, is, os, api);
    }

    // See also class StatusMessage
    public static String[] types = {
            "", "obstacle", "depot", "gold", "agent"
    };
    
    final String UP = "up";
    final String DOWN = "down";
    final String RIGHT = "right";
    final String LEFT = "left";
    final int NUMBER_OF_MINERS = 4;
    
    boolean isBusy;
    Coordinate depotPosition;
    
    ArrayList<Coordinate> minerCoordinates;
    
    public double computeDistance(Coordinate first, Coordinate second) {
    	return Math.pow(Math.pow((double)first.getX() - (double)second.getX(), 2) + 
    			        Math.pow((double)first.getY() - (double)second.getY(), 2), 0.5);
    }
    
    public double computeDistance(int x, int y, Coordinate second) {
    	return Math.pow(Math.pow((double)x - (double)second.getX(), 2) + 
    			        Math.pow((double)y - (double)second.getY(), 2), 0.5);
    }
    
    //returns index of miner closest to the current one
    public int findNearestMiner(StatusMessage status) {
    	double curMinDistance = Double.MAX_VALUE;
    	int miner = -1;
    	for(int i = 0; i < NUMBER_OF_MINERS; i++) {
    		if (i == this.getAgentId()) continue;
    		double curDistance = computeDistance(status.agentX, status.agentY, minerCoordinates.get(i));
    		if(curDistance < curMinDistance) {
    			curMinDistance = curDistance;
    			miner = i;
    		}
    	}
    	return miner;
    }
    
    public boolean isInNeighborhood(int miner) {
    	
    }
    
    public Set<String> addObstaclePosition(StatusMessage status, StatusMessage.SensorData data,  Set<String> openPaths){
    	
		if(data.x == status.agentX + 1 && data.y == status.agentY) {
			openPaths.remove(RIGHT);
		}
		if(data.x == status.agentX - 1 && data.y == status.agentY) {
			openPaths.remove(LEFT);
		}
		if(data.x == status.agentX && data.y + 1 == status.agentY) {
			openPaths.remove(DOWN);
		}
		if(data.x == status.agentX && data.y - 1 == status.agentY) {
			openPaths.remove(UP);
		}    	
    	return openPaths;
    }
    
    public Set<String> addMapLimits(StatusMessage status, Set<String> openPaths){
		if(status.width == status.agentX - 1) {
			openPaths.remove(RIGHT);
		}
		if(status.agentX == 0) {
			openPaths.remove(LEFT);
		}
		if(status.width == status.agentY - 1) {
			openPaths.remove(DOWN);
		}
		if(status.agentY == 0) {
			openPaths.remove(UP);
		}    	
    	return openPaths;
    }
     
    public Set<String> processSensorData(StatusMessage status, String preferredDirection) throws Exception {
    	Set<String> openPaths = new HashSet<String>(Arrays.asList(RIGHT, LEFT, UP, DOWN));
    	addMapLimits(status, openPaths);
    	for(StatusMessage.SensorData data : status.sensorInput) {
        	if(data.type == 1) {
        		addObstaclePosition(status, data, openPaths);
        	}        		       			         
    	}
    	return openPaths;   	
    }
    
    public StatusMessage moveRandom(String preferredDirection) throws Exception {
    	StatusMessage status = sense();
    	Set<String> openPaths = processSensorData(status, preferredDirection);
    	int randomNum = ThreadLocalRandom.current().nextInt(0, openPaths.size());
    	//log(randomNum);
    	int cur = 0;
    	for(String path : openPaths) {
    		if(cur == randomNum) {
    			switch(path) {
    				case RIGHT:
    					status = right();
    					break;
    				case LEFT:
    					status = left();
    					break;
    				case UP:
    					status = up();
    					break;
    				case DOWN:
    					status = down();
    					break;
    			}
    				
    		}
    		cur++;
    	}
    	return status;
    }
    
    public void callForHelp() {
    	
    }

    
    @Override
    public void act() throws Exception {
        sendMessage(1, new StringMessage("Hello"));

        while(true) {
            while (messageAvailable()) {
                Message m = readMessage();
                log("I have received " + m);
            }
            
            StatusMessage status = moveRandom(null);
            for(StatusMessage.SensorData data : status.sensorInput) {
            	/*
            	if(data.type == 3 && data.x == status.agentX && data.y == status.agentY){
            		pick();
            	}
            	*/
                //log(String.format("got gold!!!"));
                
            }
            
            log(String.format("I am now on position [%d,%d] of a %dx%d map.",
                    status.agentX, status.agentY, status.width, status.height));
            
            for(StatusMessage.SensorData data : status.sensorInput) {
                //log(String.format("I see %s at [%d,%d]", types[data.type], data.x, data.y));
            }


            // REMOVE THIS BEFORE SUBMITTING YOUR SOLUTION TO BRUTE !!
            //   (this is meant just to slow down the execution a bit for demonstration purposes)
            try {
                Thread.sleep(200);
            } catch(InterruptedException ie) {}
        }
    }
}
