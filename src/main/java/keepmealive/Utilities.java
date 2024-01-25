package keepmealive;

import java.util.Map;

public class Utilities {

	public static String convertMapToJson(Map<String, String> map) {
		StringBuilder jsonBuilder = new StringBuilder("{");

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			// Add key-value pair to the JSON string
			jsonBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");

			// Alternatively, if you don't want trailing comma for the last item:
			// jsonBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");
		}

		// Remove the trailing comma if there are items in the map
		if (!map.isEmpty()) {
			jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
		}

		// Close the JSON object
		jsonBuilder.append("}");

		return jsonBuilder.toString();
	}

}
