package student;

import mas.agents.Message;

public class DepotCoordinatesMessage extends Message {
	private Coordinate coordinate;

	public DepotCoordinatesMessage(Coordinate coordinate) {
		super();
		this.coordinate = coordinate;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	
}
