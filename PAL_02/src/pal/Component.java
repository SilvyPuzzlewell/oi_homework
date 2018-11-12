package pal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Component {
	int id;
	Integer blueVertex;
	HashMap<Integer, Node> nodes;
	HashMap<Integer, Integer> adjComponents;
	HashSet<Integer> adjComponentsInLongestPathsPruning;
	//I need values of inwards edges and 
	HashMap<Integer, HashMap<Integer, int[]>> inwardsEdges;
	HashMap<Integer, HashMap<Integer, int[]>> outwardsEdges;
	
	HashMap<Integer, Integer> blueVertexValueMappingOutwards;
	HashMap<Integer, Integer> blueVertexValueMappingInwards;
	
	int distance;

	//HashMap<Integer, HashSet<int[]>> evaluatedInwardsEdges;
	//HashMap<Integer, HashSet<int[]>> evaluatedOutwardsEdges;
	
	
	
	
	public Component(int id) {
		this.id = id;
		nodes = new HashMap<>();
		adjComponents = new HashMap<>();
		outwardsEdges = new HashMap<>();
		inwardsEdges = new HashMap<>();
		adjComponentsInLongestPathsPruning = new HashSet<>();
		blueVertexValueMappingOutwards = new HashMap<>();
		blueVertexValueMappingInwards = new HashMap<>();
	}
	
	
	public void addNode(Node node) {
		node.component = id;
		nodes.put(node.id, node);
	}
	
	
	public void addInwardsEdge(int from, int to, int fromComponent) {
		int[] newEdge = new int[] {from, to, -1};		
		if(inwardsEdges.containsKey(fromComponent)) {
			inwardsEdges.get(fromComponent).put(to, newEdge);
		} else {
			HashMap<Integer, int[]> bvtrb = new HashMap<>();
			bvtrb.put(to, newEdge);
			inwardsEdges.put(to, bvtrb);
		}
	}
	
	public void addOutwardsEdge(int from, int to, int toComponent) {
		int[] newEdge = new int[] {from, to, -1};		
		if(outwardsEdges.containsKey(toComponent)) {
			outwardsEdges.get(toComponent).put(from, newEdge);
		} else {
			HashMap<Integer, int[]> bvtrb = new HashMap<>();
			bvtrb.put(to, newEdge);
			outwardsEdges.put(from, bvtrb);
		}
	}
	
	/*
	public void addEvaluatedInwardsEdge(int[] evaluatedEdge, int fromComponent) {
		Set<int[]> curComponentEdges = outwardsEdges.get(fromComponent);
		curComponentEdges.remove(evaluatedEdge);
		if(curComponentEdges.isEmpty()) {
			outwardsEdges.remove(fromComponent);
		}
				
		if(evaluatedOutwardsEdges.containsKey(fromComponent)) {
			evaluatedOutwardsEdges.get(fromComponent).add(evaluatedEdge);
		} else {
			evaluatedOutwardsEdges.put(fromComponent, new HashSet<>(Arrays.asList(evaluatedEdge)));
		}
	}
	
	public void addEvaluatedOutwardsEdge(int[] evaluatedEdge, int toComponent) {
		Set<int[]> curComponentEdges = outwardsEdges.get(toComponent);
		curComponentEdges.remove(evaluatedEdge);
		if(curComponentEdges.isEmpty()) {
			outwardsEdges.remove(toComponent);
		}
				
		if(evaluatedOutwardsEdges.containsKey(toComponent)) {
			evaluatedOutwardsEdges.get(toComponent).add(evaluatedEdge);
		} else {
			evaluatedOutwardsEdges.put(toComponent, new HashSet<>(Arrays.asList(evaluatedEdge)));
		}
	}
	*/
	
	public void addAdjComponent(Integer component) {
		adjComponents.put(component, component);
	}
	
	public void addPrunedAdjComponent(Integer component) {
		adjComponentsInLongestPathsPruning.add(component);
	}
	
	//TODO value to others
}
