package keepmealive;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class SensoryInputEncoder {

	public static void translateMessage(String message, long currentSuperstep) {

		JsonObject jsonObject = parseJson(message);

		NodeRegistry nodeRegistry = NodeRegistry.getInstance();

		for (Class<? extends Node> nodeClass : nodeRegistry.getnodeTypes()) {
			String key = getKeyForNodeClass(nodeClass);

			if (jsonObject.containsKey(key)) {
				Collection<? extends Node> loadedNodes = nodeRegistry.getLoadedNodes(nodeClass);
				translateNodeClassMessage(nodeClass, jsonObject.getJsonArray(key), loadedNodes, currentSuperstep);
			}
		}

	}

	private static JsonObject parseJson(String message) {
		// Parse JSON message
		JsonObject jsonObject;
		try (JsonReader jsonReader = Json.createReader(new StringReader(message))) {
			jsonObject = jsonReader.readObject();
		}

		return jsonObject;
	}

	private static String getKeyForNodeClass(Class<? extends Node> nodeClass) {
		// Logic to determine the key based on the node class
		// This could be a method in each node class, or you can maintain a mapping
		// For simplicity, let's assume each node class has a static method called
		// getKey()
		try {
			if (nodeClass == null) {
				throw new IllegalArgumentException("Node class cannot be null");
			}
			Method getKeyMethod = nodeClass.getMethod("getKey");
			return (String) getKeyMethod.invoke(null);
		} catch (Exception e) {
			// Handle the exception appropriately based on your requirements
			e.printStackTrace();
			return null;
		}
	}

	private static void translateNodeClassMessage(Class<? extends Node> nodeClass, JsonArray jsonArray,
			Collection<? extends Node> loadedNodes, long currentSuperstep) {
		// Invoke the translateMessage method for the specific node class
		try {
			Method translateMethod = nodeClass.getMethod("translateInput", JsonArray.class, Collection.class,
					long.class);
			translateMethod.invoke(null, jsonArray, loadedNodes, currentSuperstep);
		} catch (Exception e) {
			// Handle the exception appropriately based on your requirements
			e.printStackTrace();
		}
	}
}
