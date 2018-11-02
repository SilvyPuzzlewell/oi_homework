package pal;

public class Edge implements Comparable<Edge> {
	
	private int firstNode;
	private int secondNode;
	private int weight;
	
	public Edge(int firstNode, int secondNode, int weight) {
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.weight = weight;
	}
	public int getBeginningNode() {
		return firstNode;
	}
	public int getEndNode() {
		return secondNode;
	}
	
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	//treeset uses ascending order, we want to take edges with smallest weight first
	@Override
	public int compareTo(Edge arg0) {
//		System.out.println(this.getWeight() - arg0.getWeight());
		if(this.getBeginningNode() == ((Edge) arg0).getBeginningNode() && this.getEndNode() == ((Edge) arg0).getEndNode()
	    		|| ((this.getBeginningNode() == ((Edge) arg0).getEndNode() && this.getEndNode() == ((Edge) arg0).getBeginningNode()))){
	    			return 0;
	    		}
		if(this.getWeight() - arg0.getWeight() == 0) {
			return 1;
		} else {
			return this.getWeight() - arg0.getWeight();
		}		
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!(obj instanceof Edge))
	        return false;
	    if (obj == this)
	        return true;
	   
	    return (this.getBeginningNode() == ((Edge) obj).getBeginningNode() && this.getEndNode() == ((Edge) obj).getEndNode())
	    		|| ((this.getBeginningNode() == ((Edge) obj).getEndNode() && this.getEndNode() == ((Edge) obj).getBeginningNode()));
	}
				
}
