package keepmealive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import keepmealive.node.Goal;
import keepmealive.node.Neuron;
import keepmealive.node.Olfactory;
import keepmealive.node.Stomach;

public class NodeRegistry {

	private static NodeRegistry instance;
	private List<Class<? extends Node>> nodeTypes;
	private Map<Class<? extends Node>, Collection<? extends Node>> loadedNodesMap;
	private GraphLoader graphLoader;

	// Private constructor to enforce singleton pattern
	private NodeRegistry() {
		this.nodeTypes = initNodeTypes(); // Initialize with your nodeTypes
		this.loadedNodesMap = new HashMap<>();
		this.graphLoader = new GraphLoader();
		loadNodes();
	}

	// Public method to access the singleton instance
	public static synchronized NodeRegistry getInstance() {
		if (instance == null) {
			instance = new NodeRegistry();
		}
		return instance;
	}

	// Public method to return all loaded nodes
	public Collection<Node> getAllLoadedNodes() {
		List<Node> allLoadedNodes = new ArrayList<>();
		for (Collection<? extends Node> nodes : loadedNodesMap.values()) {
			allLoadedNodes.addAll(nodes);
		}
		return allLoadedNodes;
	}

	// Hard code your subclasses here
	private List<Class<? extends Node>> initNodeTypes() {
		List<Class<? extends Node>> subclasses = new ArrayList<>();
		subclasses.add(Neuron.class);
		subclasses.add(Stomach.class);
		subclasses.add(Goal.class);
		subclasses.add(Olfactory.class);
		// Add more subclasses as needed
		return subclasses;
	}

	// Add a subclass to the list
	public void registerNodeClass(Class<? extends Node> nodeClass) {
		nodeTypes.add(nodeClass);
	}

	// Load nodes for all registered nodeTypes
	private void loadNodes() {
		for (Class<? extends Node> nodeClass : nodeTypes) {
			Collection<? extends Node> nodes = loadNodesForClass(nodeClass);
			if (nodes.isEmpty()) {
				// Log a warning instead of throwing an error
				System.out.println("Warning: No nodes loaded for class " + nodeClass.getSimpleName());
			}
			loadedNodesMap.put(nodeClass, nodes);
		}
	}

	private Collection<? extends Node> loadNodesForClass(Class<? extends Node> nodeClass) {
		return graphLoader.loadNodes(nodeClass);
	}

	// Get loaded nodes for a specific class
	public Collection<? extends Node> getLoadedNodes(Class<? extends Node> nodeClass) {
		return loadedNodesMap.getOrDefault(nodeClass, new ArrayList<>());
	}

	// Get all registered nodeTypes
	public List<Class<? extends Node>> getnodeTypes() {
		return nodeTypes;
	}
}
