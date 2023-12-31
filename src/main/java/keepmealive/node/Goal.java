package keepmealive.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import jakarta.json.JsonArray;
import keepmealive.Node;

@NodeEntity
public class Goal extends Node {

	@Property
	private String type;

	@Override
	public Map<String, String> compute(long timestep) {

		Map<String, String> result = new HashMap<>();

		double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

		if (weightedSum >= FIRING_THRESHOLD) {
			// No need to fire neuron since this is an output neuron. Just send a message.
			String goal = getGoalValueForType(type);
			result.put("Goal", goal);
		}

		return result;
	}

	private String getGoalValueForType(String type) {
		switch (type) {
		case "output_1":
			return "Left";
		case "output_2":
			return "Right";
		case "output_3":
			return "Up";
		default:
			return "";
		}
	}

	public static String getKey() {
		return "goal";
	}

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes) {
		// Does nothing

	}
}
