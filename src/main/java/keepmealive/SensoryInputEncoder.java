package keepmealive;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Collection;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class SensoryInputEncoder {

	public static void translateMessage(String message, long currentSuperstep) {

		JsonObject jsonObject = parseJson(message);

		NodeRegistry nodeRegistry = NodeRegistry.getInstance();

		// Access the values in the JsonObject
		// TODO: Move stomach logic to Javascript
		if (jsonObject.containsKey("Stomach Full")) {
			String stomachFullValue = jsonObject.getString("Stomach Full");
			System.out.println("Stomach Full value: " + stomachFullValue);
		}

		// Detected Stars
		if (jsonObject.containsKey("olfactory")) {
			JsonArray starArray = jsonObject.getJsonArray("olfactory");
			for (JsonValue starValue : starArray) {
				// Depending on the structure of your array, you might need to further process
				// each element
				String starInfo = starValue.toString();
				System.out.println("Stars: " + starInfo);
			}
		} else {
			System.out.println("No stars found");
		}

		for (Class<? extends Node> nodeClass : nodeRegistry.getnodeTypes()) {
			String key = getKeyForNodeClass(nodeClass);
			System.out.println("key: " + key);

			if (jsonObject.containsKey(key)) {
				Collection<? extends Node> loadedNodes = nodeRegistry.getLoadedNodes(nodeClass);
				System.out.println("Attempting to translate");
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
