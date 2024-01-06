package keepmealive.node;

import java.util.Collection;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import keepmealive.Node;

@NodeEntity
public class Olfactory extends Node {

	@Property
	private String type;

	private static final String TYPE_PREFIX = "input_";

	// Olfactory Range needs to match with the Phaser game
	private static final double OLFACTORY_RANGE = 150.0;

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes,
			long currentSuperstep) {

		for (JsonValue jsonValue : jsonArray) {
			if (jsonValue instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) jsonValue;
				if (jsonObject.containsKey("section")) {
					int section = jsonObject.getInt("section");
					double distance = jsonObject.getJsonNumber("distance").doubleValue();

					// Normalize the distance to be in the range [0, 1]
					double normalizedDistance = distance / OLFACTORY_RANGE;

					// Calculate the spike count based on the normalized distance
					int spikeCount = (int) Math.ceil(20 * (1.0 - normalizedDistance));

					// Section is zero based, but Neo4j is 1 based so add 1
					String nodeType = TYPE_PREFIX + (section + 1);

					// Find and update the corresponding node
					for (Node node : loadedNodes) {
						if (node instanceof Olfactory && ((Olfactory) node).getType().equals(nodeType)) {
							// Set spikeCount spikes for the node
							for (int i = 0; i < spikeCount; i++) {
								node.getFiredSupersteps().pushItem(currentSuperstep + i);
							}
							break; // Stop searching after finding the matching node
						}
					}
				}
			}
		}
	}

	public static String getKey() {
		return "olfactory";
	}

	public String getType() {
		return type != null ? type : "";
	}

}
