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

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes,
			long currentSuperstep) {
		for (JsonValue keyValue : jsonArray) {
			String starInfo = keyValue.toString();
			System.out.println("Olfactory Stars: " + starInfo);
		}

		for (JsonValue jsonValue : jsonArray) {
			if (jsonValue instanceof JsonObject) {
				JsonObject jsonObject = (JsonObject) jsonValue;
				if (jsonObject.containsKey("section")) {
					int section = jsonObject.getInt("section");
					// Section is zero based, and 1 based in Neo4j
					String nodeType = TYPE_PREFIX + (section + 1);
					// Find and update the corresponding node
					for (Node node : loadedNodes) {
						if (node instanceof Olfactory && ((Olfactory) node).getType().equals(nodeType)) {
							// Set a spike for the node
							node.getFiredSupersteps().pushItem(currentSuperstep);
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
