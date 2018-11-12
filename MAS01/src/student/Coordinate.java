package student;

import java.io.Serializable;

public class Coordinate implements Serializable {
	private int x;
	private int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Coordinate))
			return false;	
		if (obj == this)
			return true;
		Coordinate compared = (Coordinate) obj;
		return (this.x == compared.getX() && this.y == compared.getY());
	}
	
	
	public int hashCode(){
		boolean addzero = x > 9 ? false : true;
		
		StringBuilder ret = new StringBuilder();
		ret.append(Integer.toString(this.x));
		if(addzero) ret.append("0");
		ret.append(Integer.toString(this.y));
		return Integer.parseInt(ret.toString());
	}
	
	@Override
	public String toString() {
		return "Coordinate: x: " + this.x + " y: " +this.y;
	}
	
}
