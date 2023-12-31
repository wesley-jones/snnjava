package keepmealive.node;

import java.util.Collection;
import java.util.Iterator;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import jakarta.json.JsonArray;
import keepmealive.Node;

@NodeEntity
public class Stomach extends Node {

	@Property
	private String type;

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes,
			long currentSuperstep) {

		int energyLevel = 100;

		// Assuming there is only one integer value in the array
		if (jsonArray != null && jsonArray.size() > 0) {
			// Get the first (and presumably only) element from the array
			energyLevel = jsonArray.getInt(0);
		}

		int totalNodes = loadedNodes.size();

		// Determine the number of nodes to set spikes based on the jsonValue reduction
		int numNodesToSetSpikes = calculateNodesToSetSpikes(energyLevel, totalNodes);
		System.out.println("Nodes to spike: " + numNodesToSetSpikes);

		// Iterate through the loadedNodes and set spikes on the selected nodes
		Iterator<? extends Node> iterator = loadedNodes.iterator();
		for (int i = 0; i < numNodesToSetSpikes && iterator.hasNext(); i++) {
			Node node = iterator.next();
			node.getFiredSupersteps().pushItem(currentSuperstep);
		}
	}

//		for (JsonValue jsonValue : jsonArray) {
//			System.out.println("jsonValue: " + jsonValue);
//			if (jsonValue instanceof JsonObject) {
//				JsonObject jsonObject = (JsonObject) jsonValue;
//				if (jsonObject.containsKey("section")) {
//					int section = jsonObject.getInt("section");
//					// Section is zero based, but Neo4j is 1 based so add 1
//					String nodeType = TYPE_PREFIX + (section + 1);
//					// Find and update the corresponding node
//					for (Node node : loadedNodes) {
//						if (node instanceof Olfactory && ((Olfactory) node).getType().equals(nodeType)) {
//							// Set a spike for the node
//							node.getFiredSupersteps().pushItem(currentSuperstep);
//							break; // Stop searching after finding the matching node
//						}
//					}
//				}
//			}
//	}

	private static int calculateNodesToSetSpikes(int jsonValue, int totalNodes) {
		// Calculate the inverted ratio (1 - ratio)
		double invertedRatio = 1.0 - ((double) jsonValue / 100);

		// Use the inverted ratio to determine the number of nodes to set spikes
		int numNodesToSetSpikes = (int) Math.ceil(invertedRatio * totalNodes);

		return numNodesToSetSpikes;
	}

	public static String getKey() {
		return "energyLevel";
	}
}
