package pal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DoStuff01 {

	HashMap<Integer, HashMap<Integer, Edge>> outOfComponent = new HashMap<Integer, HashMap<Integer, Edge>>();
	PriorityQueue<Edge> betweenComponents = new PriorityQueue<Edge>();
	HashSet<Edge> inComponent = new HashSet<Edge>();
	HashSet<Integer> verticesInComponent = new HashSet<Integer>();
	TreeSet<Edge> sortedEdges = new TreeSet<>();
	HashMap<Integer, Integer> predictSumMap = new HashMap<>();
	int numberOfEdges;
	int numberOfVertices;
	int beginning;
	int end;

	int spanningTreeSum = 0;
	int distance = 0;
	int totalDistance = 0;
	int predictSum = 0;
	
	int spanningTreeMinimum = Integer.MAX_VALUE / 2;
	public void addGraphNode(int firstNode, int secondNode, int weight) {

		Edge newEdge = new Edge(firstNode, secondNode, weight);
		if (outOfComponent.containsKey(firstNode)) {
			outOfComponent.get(firstNode).put(secondNode, newEdge);
		} else {
			HashMap<Integer, Edge> edges = new HashMap<Integer, Edge>();
			edges.put(secondNode, newEdge);
			outOfComponent.put(firstNode, edges);
		}

		if (outOfComponent.containsKey(secondNode)) {
			outOfComponent.get(secondNode).put(firstNode, newEdge);
		} else {
			HashMap<Integer, Edge> edges = new HashMap<Integer, Edge>();
			edges.put(firstNode, newEdge);
			outOfComponent.put(secondNode, edges);
		}
		
		sortedEdges.add(newEdge);
	}

	public void jarnikAlgorithm() {
		// TODO number of vertices

		while (inComponent.size() < numberOfVertices - 1) {
			Edge newOne = betweenComponents.poll();
			int newNode = chooseDestinationNodeSet(newOne);

			if (outOfComponent.get(newNode) != null) {
				for (Edge edge : outOfComponent.get(newNode).values()) {
					if (verticesInComponent.contains(chooseDestinationNode(newNode, edge))) {
						if (!edge.equals(newOne)) {
							betweenComponents.remove(edge);
						} else {
							inComponent.add(edge);
							spanningTreeSum += edge.getWeight();
							if(predictSumCondition(spanningTreeSum, inComponent.size())){
								return;
							}								
						}
					} else {
						betweenComponents.add(edge);
					}
				}
			}
			verticesInComponent.add(newNode);
		}
		spanningTreeMinimum = spanningTreeSum;
		
	}

	private int chooseDestinationNode(int sourceNode, Edge edge) {
		if (edge.getBeginningNode() != sourceNode) {
			return edge.getBeginningNode();
		} else {
			return edge.getEndNode();
		}
	}

	private int chooseDestinationNodeSet(Edge edge) {
		if (verticesInComponent.contains(edge.getBeginningNode())) {
			return edge.getEndNode();
		} else {
			return edge.getBeginningNode();
		}
	}

	// edited bfs for shortest path search between two nodes
	void BFS(int beginningNode, int finalDestinationNode) {
		// Create a queue for BFS
		// keeps track of lvl in which a node was found too
		
		long startTime = System.nanoTime();    
		// ... the code being measured ...    
		
		HashMap<Integer, Integer> visited = new HashMap<Integer, Integer>();
		HashMap<Integer, HashSet<Integer>> parentMapping = new HashMap<Integer, HashSet<Integer>>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		HashSet<Edge> finalEdges = new HashSet<Edge>();

		queue.add(beginningNode);
		visited.put(beginningNode, 0);

		int currentNode;
		int curLvl = 0;
		int finalLevel = Integer.MAX_VALUE;

		while (queue.size() != 0) {
			currentNode = queue.poll();
			curLvl = visited.get(currentNode);
			if (finalLevel == curLvl) {
				break;
			}
			Iterator<Edge> currentFoundEdges;
			if (outOfComponent.containsKey(currentNode)) {
				currentFoundEdges = outOfComponent.get(currentNode).values().iterator();
			} else {
				continue;
			}
			while (currentFoundEdges.hasNext()) {
				Edge currentFoundEdge = currentFoundEdges.next();
				int sourceNode = currentNode;
				int destinationNode = chooseDestinationNode(sourceNode, currentFoundEdge);
				if (destinationNode == finalDestinationNode) {
					finalEdges.add(currentFoundEdge);
					finalLevel = curLvl + 1;
				}
				if (!visited.containsKey(destinationNode)) {
					visited.put(destinationNode, curLvl + 1);
					HashSet<Integer> parents = new HashSet<Integer>();
					parents.add(sourceNode);
					parentMapping.put(destinationNode, parents);
					queue.add(destinationNode);
				} else if (visited.get(destinationNode) > curLvl) {
					parentMapping.get(destinationNode).add(sourceNode);
				}
			}
		}

		// backtrack
		HashMap<Integer, HashSet<Integer>> pathsParentMapping = new HashMap<>();
		ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
		ArrayList<Integer[]> pathCosts = new ArrayList<>();
		queue.add(end);
		int curIndex = -1;
		int m = 0;
		for (int parent : parentMapping.get(end)) {
			ArrayList<Integer> nextPath = new ArrayList<>();
			nextPath.add(end);
			nextPath.add(parent);
			pathCosts.add(new Integer[] {m, getEdgeFromMap(end, parent).getWeight()});
			m++;
			paths.add(nextPath);
		}

		for (int i = curLvl - 1; i > 0; i--) {
			for (int k = 0; k < paths.size(); k++) {
				ArrayList<Integer> path = paths.get(k);
				HashSet<Integer> parents = parentMapping.get(path.get(path.size() - 1));
				int j = 0;
				if(parents != null) {
					int oldCost = 0;
					for (int parent : parents) {
						int sourceNode = path.get(path.size() - 1 - j);						
						if (j == 0) {
							Integer cost = pathCosts.get(k)[1];
							oldCost = cost;
							cost += getEdgeFromMap(sourceNode, parent).getWeight();
							pathCosts.get(k)[1] = cost;
							path.add(parent);
							j++;
							
						} else {
							Integer cost = oldCost;
							cost += getEdgeFromMap(sourceNode, parent).getWeight();						
							pathCosts.add(new Integer[] {m, cost});
							m++;
							ArrayList<Integer> nextPath = new ArrayList<>(path.subList(0, path.size() - j));
							nextPath.add(parent);
							paths.add(nextPath);
						}
						
	
					}
				}
			}
		}
		
		Collections.sort(pathCosts, new Comparator<Integer[]>() {
		    @Override
		    public int compare(Integer[] first, Integer[] second) {
		        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
		        return first[1] > second[1] ? 1 : -1;
		    }
		});
		
		
		long estimatedTime = System.nanoTime() - startTime;
		for(int i = 1; i < numberOfVertices; i++) {
			predictSum += sortedEdges.pollFirst().getWeight();
			predictSumMap.put(i, predictSum);
		}
		System.out.println("BFS Time: " + estimatedTime);
		
		
		//HashMap<Integer, HashMap<Integer, Edge>> outOfComponent = new HashMap<Integer, HashMap<Integer, Edge>>();
		for(int i = 0; i < pathCosts.size(); i++) {
			
			startTime = System.nanoTime(); 
			boolean shit = createComponent(paths.get(pathCosts.get(i)[0]));
			estimatedTime = System.nanoTime() - startTime;
			System.out.println("Component time " + i + ": " + estimatedTime);
			
			startTime = System.nanoTime();
			if(shit) {
				jarnikAlgorithm();
			}
			System.out.println(totalDistance + " " + spanningTreeMinimum);
			estimatedTime = System.nanoTime() - startTime;
			System.out.println("Jarnik time " + i + ": " + estimatedTime);
			
			//estimatedTime = System.nanoTime() - startTime;
			//System.out.println("Jarnik time " + i + ": " + estimatedTime);
			//startTime = System.nanoTime();	
			destroyComponent();
			estimatedTime = System.nanoTime() - startTime;
			//System.out.println("Destruction time " + i + ": " + estimatedTime);
		}
		
		System.out.println(totalDistance + " " + spanningTreeMinimum);
		
		/*
		while (!queue.isEmpty()) {
			curIndex = queue.poll();
			if (parentMapping.get(curIndex) != null) {

				queue.addAll(parentMapping.get(curIndex));
				pathsParentMapping.put(curIndex, parentMapping.get(curIndex));
			}
		}

		backtrack(pathsParentMapping);
		*/
	}

	class Node {
		int id;
		int weight;
		int parent;

		public Node(int weight, int parent, int id) {
			this.weight = weight;
			this.parent = parent;
			this.id = id;
		}

	}

	private Edge getEdgeFromMap(int from, int to) {
		return outOfComponent.get(from).get(to);
	}
	
	private boolean predictSumCondition(int spanningTreeSum, int spanningTreeSize) {
		if(numberOfVertices- 1 - spanningTreeSize <= 0){
			return spanningTreeSum > spanningTreeMinimum;
		}
//		System.out.println("spanningTreeSum " + spanningTreeSum + " spanningTreeMinimum " + spanningTreeMinimum + " predictSum " + 
//				predictSumMap.get(numberOfVertices- 1 - spanningTreeSize) +
//				 " curSum " + (spanningTreeSum +  predictSumMap.get(numberOfVertices- 1 - spanningTreeSize)));		
		return spanningTreeSum +  predictSumMap.get(numberOfVertices- 1 - spanningTreeSize) > spanningTreeMinimum;
	}
	public boolean createComponent(ArrayList<Integer> settledNodes) {
		int curIndex = 0;
		Edge parentEdge = null;
		Edge previousParentEdge = null;
		
		//verticesInComponent.add(end);
		while (true) {
			//load edge
			int cur = settledNodes.get(curIndex);
			int parent = settledNodes.get(curIndex + 1);
			parentEdge = getEdgeFromMap(cur, parent);
			
			
			//add to spanning tree
			spanningTreeSum += parentEdge.getWeight();
			
			distance += 1;
			inComponent.add(parentEdge);
			verticesInComponent.add(cur);
			if(predictSumCondition(spanningTreeSum, inComponent.size())){
				return false;
			}
			
			//add stuff to between component struct
			if (outOfComponent.get(cur) != null) {
				for (Edge edge : outOfComponent.get(cur).values()) {
					if (!edge.equals(parentEdge) && !edge.equals(previousParentEdge)) {
						betweenComponents.add(edge);
					}
				}
			}

			if (parentEdge.getEndNode() == cur) {
				cur = parentEdge.getBeginningNode();
			} else {
				cur = parentEdge.getEndNode();
			}
			previousParentEdge = parentEdge;
			curIndex++;
			if(curIndex + 1 >= settledNodes.size()) {
				break;
			}
		}
		
		verticesInComponent.add(beginning);
		if (outOfComponent.get(beginning) != null) {
			for (Edge edge : outOfComponent.get(beginning).values()) {
				if (!edge.equals(previousParentEdge)) {
					betweenComponents.add(edge);
				}
			}
		}
		totalDistance = distance;
		return true;
		
	}
	
	public void destroyComponent() {
		distance = 0;
		spanningTreeSum = 0;		
		betweenComponents.clear();
		inComponent.clear();
		verticesInComponent.clear();
	}
	
	/*
	 * 
	 * /*
	// dijkstra for finding path with lowest cost
	void backtrack(HashMap<Integer, HashSet<Integer>> parentMapping) {

		Map<Integer, Node> settledNodes = new HashMap<>();
		Map<Integer, Node> unsettledNodes = new HashMap<>();

		unsettledNodes.put(end, new Node(0, -1, end));

		while (unsettledNodes.size() != 0) {
			Node currentNode = getLowestDistanceNode(unsettledNodes);
			unsettledNodes.remove(currentNode.id);
			if (parentMapping.get(currentNode.id) != null) {
				for (int childNode : parentMapping.get(currentNode.id)) {
					Node adjacentNode = null;
					Edge currentEdge = getEdgeFromMap(currentNode.id, childNode);

					if (unsettledNodes.containsKey(childNode)) {
						adjacentNode = unsettledNodes.get(childNode);
					} else if (settledNodes.containsKey(childNode)) {
						adjacentNode = settledNodes.get(childNode);
					} else {
						adjacentNode = new Node(currentNode.weight + currentEdge.getWeight(), currentNode.id,
								childNode);
						unsettledNodes.put(childNode, adjacentNode);
					}
					if (!settledNodes.containsKey(childNode)) {

						calculateMinimumDistance(adjacentNode, currentEdge, currentNode);

					}
				}
			}
			settledNodes.put(currentNode.id, currentNode);
		}
		createComponent(settledNodes);
	}
	
	public void createComponent(Map<Integer, Node> settledNodes) {
		int curIndex = beginning;
		Edge parentEdge = null;
		Edge previousParentEdge = null;
		verticesInComponent.add(beginning);
		verticesInComponent.add(end);
		while (true) {
			Node cur = settledNodes.get(curIndex);
			if (cur.parent == -1) {
				parentEdge = null;
			} else {
				parentEdge = getEdgeFromMap(curIndex, cur.parent);
				spanningTreeSum += parentEdge.getWeight();
				distance += 1;
				inComponent.add(parentEdge);
				verticesInComponent.add(cur.id);
			}

			if (outOfComponent.get(curIndex) != null) {
				for (Edge edge : outOfComponent.get(curIndex).values()) {
					if (!edge.equals(parentEdge) && !edge.equals(previousParentEdge)) {
						betweenComponents.add(edge);
					}
				}
			}
			if (parentEdge == null) {
				break;
			}
			if (parentEdge.getEndNode() == curIndex) {
				curIndex = parentEdge.getBeginningNode();
			} else {
				curIndex = parentEdge.getEndNode();
			}
			previousParentEdge = parentEdge;
		}
		jarnikAlgorithm();
	}
	
	private Node getLowestDistanceNode(Map<Integer, Node> unsettledNodes) {
		Node lowestDistanceNode = null;
		int lowestDistance = Integer.MAX_VALUE;
		for (Node node : unsettledNodes.values()) {
			int nodeDistance = node.weight;
			if (nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}

	private static void calculateMinimumDistance(Node evaluationNode, Edge edge, Node sourceNode) {
		Integer sourceDistance = sourceNode.weight;
		if (sourceDistance + edge.getWeight() < evaluationNode.weight) {
			evaluationNode.weight = sourceDistance + edge.getWeight();
			evaluationNode.parent = sourceNode.id;
		}
	}
	*/
}
