package student;

import java.util.Set;

import mas.agents.Message;

public class AgentStatusMessage extends Message {
	private Coordinate coordinate;
	private String agentStatus;
	private Set<String> openPaths;
	private boolean goldFound;

	public boolean isGoldFound() {
		return goldFound;
	}

	public void setGoldFound(boolean goldFound) {
		this.goldFound = goldFound;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public AgentStatusMessage(Coordinate coordinate, String agentStatus, Set<String> openPaths, boolean goldFound) {
		super();
		this.coordinate = coordinate;
		this.agentStatus = agentStatus;
		this.openPaths = openPaths;
		this.goldFound = goldFound;
	}
	
	public String getAgentStatus() {
		return agentStatus;
	}

	public void setAgentStatus(String agentStatus) {
		this.agentStatus = agentStatus;
	}

	public Set<String> getOpenPaths() {
		return openPaths;
	}

	public void setOpenPaths(Set<String> openPaths) {
		this.openPaths = openPaths;
	}
	
		
}
