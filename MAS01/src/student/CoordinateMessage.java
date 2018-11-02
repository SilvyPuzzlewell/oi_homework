package student;

import mas.agents.Message;

public class CoordinateMessage extends Message {
	private Coordinate coordinate;

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public CoordinateMessage(Coordinate coordinate) {
		super();
		this.coordinate = coordinate;
	}	
}
