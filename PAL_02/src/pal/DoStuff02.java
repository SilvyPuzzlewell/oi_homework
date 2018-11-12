package pal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class DoStuff02 {

	Stack<Integer> dfsStack = new Stack<>();
	Set<Integer> dfsVisitedSet = new HashSet<>();

	int nVertices;
	int nBlueVertices;
	int nEdges;
	HashSet<Integer> blueVertices = new HashSet<>();
	// TODO possibility of adding backward vertices
	ArrayList<Node> graph = new ArrayList<>();
	ArrayList<Node> reversedGraph = new ArrayList<>();
	ArrayList<Component> condensedGraph = new ArrayList<>();

	// ---PART1: put shit into components and create condensation, using Tarjan
	int curId = 0;
	int componentCounter = 0;
	int[] ids; // dfs sequence
	int[] lows; // lowlinks
	boolean[] onStack; // map to stack

	public void setArrays() {
		ids = new int[nVertices]; // dfs sequence
		lows = new int[nVertices]; // lowlinks
		onStack = new boolean[nVertices];
	}

	// TODO check if just int is faster
	public void tarjan() {
		for (Node node : graph) {
			if (!dfsVisitedSet.contains(node.id)) {
				tarjanRecursion(node);
			}
		}

		createCondenseGraphConnections();
	}

	int tarjanCounter = 0;

	public void tarjanRecursion(Node curNode) {
		tarjanCounter++;
		dfsStack.add(curNode.id);
		onStack[curNode.id] = true;
		ids[curNode.id] = curId;
		lows[curNode.id] = curId;
		dfsVisitedSet.add(curNode.id);
		curId++;

		ArrayList<Node> curAdjList = graph.get(curNode.id).adjList;
		if (curAdjList != null && !curAdjList.isEmpty()) {
			for (Node adjNode : curAdjList) {
				if (!dfsVisitedSet.contains(adjNode.id)) {
					tarjanRecursion(adjNode);
				}
				if (onStack[adjNode.id]) {
					lows[curNode.id] = lowLink(lows[curNode.id], lows[adjNode.id]);
				}
			}

		}
		if (ids[curNode.id] == lows[curNode.id]) {
			Component newComponent = new Component(componentCounter);
			newComponent.addNode(curNode);
			componentCounter++;

			while (!dfsStack.isEmpty()) {
				Integer node = dfsStack.pop();
				onStack[node] = false;
				lows[node] = ids[curNode.id];
				newComponent.addNode(graph.get(node));
				if (node == curNode.id)
					break;
			}
			condensedGraph.add(newComponent);
		}
	}

	private int lowLink(int first, int second) {
		int ret = first < second ? first : second;
		return ret;
	}

	private void createCondenseGraphConnections() {
		for (Component component : condensedGraph) {
			for (Node node : component.nodes.values()) {
				if (blueVertices.contains(node.id)) {
					component.blueVertex = node.id;
				}
				for (Node adjNode : node.adjList) {
					if (component.id != adjNode.component) {
						addEdgeConnection(node, adjNode);
					}

					if (!component.adjComponents.containsKey(adjNode.component) && component.id != adjNode.component) {
						component.addAdjComponent(adjNode.component);
					}
				}
			}
		}
	}

	private void addEdgeConnection(Node from, Node to) {
		condensedGraph.get(from.component).addOutwardsEdge(from.id, to.id, to.component);
		condensedGraph.get(to.component).addInwardsEdge(from.id, to.id, from.component);
	}

	// ---

	// ---part2: traverse component graph, find longest paths; input - condensed
	// graph with mapping of starting nodes, output - paths containining the
	// largerst number of components
	// TODO implement dynamic programming approach if bottlenecking
	int vertexCounter = 0;

	public void findAllLongestPaths() {
		int globalMaxLevel = 0;
		for (int i = 0; i < condensedGraph.size(); i++) {
			if (condensedGraph.get(i).inwardsEdges.isEmpty()) {

				int currentMaxLevel = findLongestPaths(i, globalMaxLevel);

				if (currentMaxLevel != -1)
					globalMaxLevel = currentMaxLevel;
			}
		}

		superGlobalMaxLevel = globalMaxLevel;
		copyLongestPaths(superGlobalMaxLevel);
	}

	int superGlobalMaxLevel;

	HashSet<Component> pruningStartComponents = new HashSet<>();
	HashSet<Component> mainComponents = new HashSet<>();
	public void copyLongestPaths(int level) {
		ArrayList<ArrayList<int[]>> longestPathsinternal = longestPaths.get(level);
		for (ArrayList<int[]> arrayList : longestPathsinternal) {
			for (int i = 0; i < arrayList.size(); i++) {
				int[] integer = arrayList.get(i);
				condensedGraph.get(integer[0]).addPrunedAdjComponent(integer[1]);
				mainComponents.add(condensedGraph.get(integer[1]));
				if (i == arrayList.size() - 1) {
					if (!condensedGraph.get(integer[0]).inwardsEdges.isEmpty()) {
						System.out.println("Sanity CHECK");
					} else {
						pruningStartComponents.add(condensedGraph.get(integer[0]));
						mainComponents.add(condensedGraph.get(integer[1]));
					}
				}
			}
		}
		// condensedGraph.get(visitedSet[currentIndex][1]).addPrunedAdjComponent(
	}

	HashMap<Integer, ArrayList<ArrayList<int[]>>> longestPaths = new HashMap<>();

	int longestPathCounter = 0;
	int longestPathCounter2 = 0;

	public int findLongestPaths(Integer startNode, int globalMaxLevel) {
		Queue<Integer> queue = new LinkedList<Integer>();
		int[][] visitedSet = new int[condensedGraph.size()][3]; // [0] - 0 == unvisited; 1 == visited;[1] parent;[2]
																// backtrack level
		HashMap<Integer, ArrayList<Integer>> levelMapping = new HashMap<>(); // mapping of highest indices to visited
																				// set, potential easy speedup
		queue.add(startNode);
		visitedSet[startNode][1] = -1; // no parent
		visitedSet[startNode][2] = 0;

		int maxLevel = 0;
		while (!queue.isEmpty()) {

			// for(int i = 0; i < condensedGraph.size(); i++) {
			// System.out.print(visitedSet[i][0] + " ");
			// }
			Integer curNode = queue.poll();
			longestPathCounter++;

			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			Collection<Integer> adjNodes = condensedGraph.get(curNode).adjComponents.values();
			if (!adjNodes.isEmpty()) {
				for (Integer adjNode : condensedGraph.get(curNode).adjComponents.values()) {
					// visited
					// fugin loops
					if (visitedSet[curNode][1] != -1
							&& visitedSet[curNode][2] <= visitedSet[visitedSet[curNode][1]][2]) {
						visitedSet[curNode][2] = visitedSet[visitedSet[curNode][1]][2] + 1;
					}
					if (visitedSet[adjNode][0] == 1) {
						// longer path found
						if (visitedSet[adjNode][2] < visitedSet[curNode][2] + 1) {
							// update parent and level
							visitedSet[adjNode][1] = curNode; // add link to parent
							visitedSet[adjNode][2] = visitedSet[curNode][2] + 1; // increase level
						}
					} else {
						visitedSet[adjNode][0] = 1;
						queue.add(adjNode);
						visitedSet[adjNode][1] = curNode; // add link to parent
						visitedSet[adjNode][2] = visitedSet[curNode][2] + 1; // increase level
					}
				}
			}

			maxLevel = visitedSet[curNode][2];

		}

		// backtrack
		if (maxLevel >= globalMaxLevel) {
			for (int i = 0; i < condensedGraph.size(); i++) {
				if (visitedSet[i][2] == maxLevel) {
					// backtrack here
					int currentIndex = i;
					ArrayList<int[]> longestPath = new ArrayList<>();
					while (visitedSet[currentIndex][1] != -1) {
						longestPathCounter2++;
						if (!longestPaths.containsKey(maxLevel)) {
							ArrayList<ArrayList<int[]>> levelLongestPathsContainer = new ArrayList<>();
							longestPaths.put(maxLevel, levelLongestPathsContainer);
						}
						longestPath.add(new int[] { visitedSet[currentIndex][1], currentIndex });
						currentIndex = visitedSet[currentIndex][1];

						/*
						 * condensedGraph.get(visitedSet[currentIndex][1]).addPrunedAdjComponent(
						 * visitedSet[currentIndex][0]); currentIndex = visitedSet[currentIndex][1];
						 */

					}

					longestPaths.get(maxLevel).add(longestPath);
				}
			}
			return maxLevel;
		}
		return -1;

	}

	// ---

	// part3 evaluate connections on
	public void premapBlueVerticesPaths() {
		for (Component componentR : pruningStartComponents) {
			Integer component = componentR.id;
			Queue<Integer> queue = new LinkedList<>();
			Set<Integer> visited = new HashSet<>();
			queue.add(component);
			visited.add(component);
			while (!queue.isEmpty()) {
				Integer curComponent = queue.poll();
				HashSet<Integer> adjancent = condensedGraph.get(curComponent).adjComponentsInLongestPathsPruning;

				Component curComponentObj = condensedGraph.get(curComponent);
				premapBlueVertexPathsInwards(curComponentObj.blueVertex, curComponentObj);
				premapBlueVertexPathsOutwards(curComponentObj.blueVertex, curComponentObj);
				if (!adjancent.isEmpty()) {
					for (Integer componentAdj : adjancent) {
						if (!visited.contains(componentAdj)) {
							visited.add(componentAdj);
							queue.add(componentAdj);
						}
					}
				}

			}

		}
		
		for (Component componentR : pruningStartComponents) {
			DIJSKTRA(componentR);
			for(Component componentW : mainComponents) {
				System.out.println("DISTANCE " + componentW.distance);
			}
		}

		

	}

	public void DIJSKTRA(Component source) {
		source.distance = 0;

		Set<Component> settledComponents = new HashSet<>();
		Set<Component> unsettledComponents = new HashSet<>();

		unsettledComponents.add(source);

		while (unsettledComponents.size() != 0) {
			Component currentComponent = getLowestDistanceComponent(unsettledComponents);
			unsettledComponents.remove(currentComponent);

			for (Integer componentAdjId : currentComponent.adjComponentsInLongestPathsPruning) {
				Component adjComponentObj = condensedGraph.get(componentAdjId);
				int edgeWeight = computeValue(currentComponent, adjComponentObj);
				if (!settledComponents.contains(adjComponentObj)) {
					calculateMinimumDistance(adjComponentObj, edgeWeight, currentComponent);
					unsettledComponents.add(adjComponentObj);
				}
			}

			settledComponents.add(currentComponent);
			/*
			 * for (Entry < Node, Integer> adjacencyPair:
			 * currentNode.getAdjacentNodes().entrySet()) { Node adjacentNode =
			 * adjacencyPair.getKey(); Integer edgeWeight = adjacencyPair.getValue(); if
			 * (!settledNodes.contains(adjacentNode)) {
			 * calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
			 * unsettledNodes.add(adjacentNode); } } settledNodes.add(currentNode);
			 */
		}
	}

	private int computeValue(Component from, Component to) {
		HashMap<Integer, Integer> outWardsMapping = from.blueVertexValueMappingOutwards;
		HashMap<Integer, int[]> outwardsEdges = from.outwardsEdges.get(to.id);
		HashMap<Integer, Integer> inWardsMapping = to.blueVertexValueMappingInwards;
		HashMap<Integer, int[]> inwardsEdges = to.outwardsEdges.get(from.id);

		int minValue = Integer.MAX_VALUE;
		if(outwardsEdges != null && outwardsEdges.entrySet() != null &&  !outwardsEdges.entrySet().isEmpty()) {
		for (Map.Entry<Integer, int[]> outward : outwardsEdges.entrySet()) {
				int value = outWardsMapping.get(outward.getValue()[0]) + 1
						+ inWardsMapping.get(outward.getValue()[1]);
				if (value < minValue) {
					minValue = value;
				}
			}
		}
		
		return minValue;

	}

	private Component getLowestDistanceComponent(Set<Component> unsettledComponents) {
		Component lowestDistanceComponent = null;
		int lowestDistance = Integer.MAX_VALUE;
		for (Component component : unsettledComponents) {
			int componentDistance = component.distance;
			if (componentDistance < lowestDistance) {
				lowestDistance = componentDistance;
				lowestDistanceComponent = component;
			}
		}
		return lowestDistanceComponent;
	}

	private void calculateMinimumDistance(Component evaluationNode, Integer edgeWeigh, Component sourceNode) {
		Integer sourceDistance = sourceNode.distance;
		if (sourceDistance + edgeWeigh < evaluationNode.distance) {
			evaluationNode.distance = (sourceDistance + edgeWeigh);
			/*
			 * LinkedList<Node> shortestPath = new
			 * LinkedList<>(sourceNode.getShortestPath()); shortestPath.add(sourceNode);
			 * evaluationNode.setShortestPath(shortestPath);
			 */
		}
	}

	int blueCounter = 0;

	public void premapBlueVertexPathsOutwards(Integer blueVertex, Component curComponent) {
		Queue<Integer> queue = new LinkedList<Integer>();
		HashMap<Integer, Integer> visitedSet = new HashMap<Integer, Integer>();
		// int[][] visitedSet = new int[nVertices][2]; // [0]
		HashMap<Integer, Integer> mapping = new HashMap<>(); // mapping of highest
		// indices to visited set, potential easy speedup
		queue.add(blueVertex);
		visitedSet.put(blueVertex, 0);

		while (!queue.isEmpty()) {
			Integer curNode = queue.poll();
			Collection<Node> adjNodes = graph.get(curNode).adjList; // condensedGraph.get(curNode).adjComponents.values();
			if (!adjNodes.isEmpty()) {
				for (Node adjNodeR : adjNodes) {
					int adjNode = adjNodeR.id;
					// System.out.println(queue);
					// System.out.println("AdjNode " + adjNode + " visited " +
					// visitedSet.containsKey(adjNode));
					// blueCounter++;

					if (!curComponent.nodes.containsKey(adjNode)) {
						continue;
					}
					// visited
					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

					if (!visitedSet.containsKey(adjNode)) {
						visitedSet.put(adjNode, visitedSet.get(curNode) + 1);
						queue.add(adjNode);
					}
				}
			}
		}

		curComponent.blueVertexValueMappingOutwards = visitedSet;
	}

	int blueCounter2 = 0;
	long arrayTime = 0;

	public void premapBlueVertexPathsInwards(Integer blueVertex, Component curComponent) {
		Queue<Integer> queue = new LinkedList<Integer>();
		HashMap<Integer, Integer> visitedSet = new HashMap<Integer, Integer>();
		// int[][] visitedSet = new int[nVertices][2]; // [0]
		HashMap<Integer, Integer> mapping = new HashMap<>(); // mapping of highest
		// indices to visited set, potential easy speedup
		queue.add(blueVertex);
		visitedSet.put(blueVertex, 0);

		while (!queue.isEmpty()) {
			Integer curNode = queue.poll();
			Collection<Node> adjNodes = reversedGraph.get(curNode).adjList; // condensedGraph.get(curNode).adjComponents.values();
			if (!adjNodes.isEmpty()) {
				for (Node adjNodeR : adjNodes) {
					blueCounter++;
					int adjNode = adjNodeR.id;
					if (!curComponent.nodes.containsKey(adjNode)) {
						continue;
					}
					// visited
					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// System.out.println(queue);
					// System.out.println("AdjNode " + adjNode + " visited " +
					// visitedSet[adjNode][0]);
					if (!visitedSet.containsKey(adjNode)) {
						visitedSet.put(adjNode, visitedSet.get(curNode) + 1);
						queue.add(adjNode);
					}
				}
			}
		}

		curComponent.blueVertexValueMappingInwards = visitedSet;
	}

	public void findShortestPath() {
		for (int i = 0; i < condensedGraph.size(); i++) {
			if (condensedGraph.get(i).inwardsEdges.isEmpty()) {
				// findLongestPaths(i);
			}
		}
	}

	/*
	 * public void mapValues(int[][] visitedSet, Component curComponent) { for
	 * (Map.Entry<Integer, HashSet<int[]>> entry :
	 * curComponent.outwardsEdges.entrySet()) { for (int[] outwardEdge :
	 * entry.getValue()) {
	 * 
	 * } }
	 * 
	 * for (Map.Entry<Integer, HashSet<int[]>> entry :
	 * curComponent.inwardsEdges.entrySet()) { for (int[] inwardEdge :
	 * entry.getValue()) { if(inwardEdge[0] == node) { inwardEdge[2] = distance;
	 * curComponent.addEvaluatedOutwardsEdge(inwardEdge, entry.getKey()); } return
	 * true; } } }
	 */

	/*
	 * private boolean allEmpty
	 * 
	 * private boolean checkTargetNode(int node,int distance, Component
	 * curComponent){ for (Map.Entry<Integer, HashSet<int[]>> entry :
	 * curComponent.outwardsEdges.entrySet()) { for (int[] outwardEdge :
	 * entry.getValue()) { if(outwardEdge[0] == node) { outwardEdge[2] = distance;
	 * curComponent.addEvaluatedOutwardsEdge(outwardEdge, entry.getKey()); } return
	 * true; } }
	 * 
	 * for (Map.Entry<Integer, HashSet<int[]>> entry :
	 * curComponent.inwardsEdges.entrySet()) { for (int[] inwardEdge :
	 * entry.getValue()) { if(inwardEdge[0] == node) { inwardEdge[2] = distance;
	 * curComponent.addEvaluatedOutwardsEdge(inwardEdge, entry.getKey()); } return
	 * true; } }
	 * 
	 * return false; }
	 */

}
