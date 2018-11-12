package student;

import mas.agents.Message;

public class PickupHelpRequestMessage extends Message {
	private Coordinate senderCoordinate;
	private Coordinate goldCoordinate;

	public Coordinate getSenderCoordinate() {
		return senderCoordinate;
	}

	public void setSenderCoordinate(Coordinate senderCoordinate) {
		this.senderCoordinate = senderCoordinate;
	}

	public PickupHelpRequestMessage(Coordinate senderCoordinate) {
		super();
		this.senderCoordinate = senderCoordinate;
	}
	
}
