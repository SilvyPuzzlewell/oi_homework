package student;

import java.util.Set;

public class ExploredCoordinateMessage {
	private Coordinate coordinate;
	private Set<String> openPaths;
	public Coordinate getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	public Set<String> getOpenPaths() {
		return openPaths;
	}
	public void setOpenPaths(Set<String> openPaths) {
		this.openPaths = openPaths;
	}
	public ExploredCoordinateMessage(Coordinate coordinate, Set<String> openPaths) {
		super();
		this.coordinate = coordinate;
		this.openPaths = openPaths;
	}
	
	
}
