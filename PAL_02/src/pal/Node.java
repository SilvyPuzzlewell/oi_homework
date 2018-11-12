package pal;

import java.util.ArrayList;

public class Node {
	int id;
	int component;
	ArrayList<Node> adjList;
	
	public Node(int id) {
		this.id = id;
		adjList = new ArrayList<>();
	}
	
	public void addAdjNode(Node node) {
		adjList.add(node);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Node))
			return false;	
		if (obj == this)
			return true;
		Node objNode = (Node)obj;
		return this.id == objNode.id;
	}
	
	
	public int hashCode(){
		return id;
	}

}
