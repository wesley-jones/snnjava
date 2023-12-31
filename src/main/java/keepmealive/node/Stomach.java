package keepmealive.node;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import keepmealive.Constants;
import keepmealive.LimitedStack;
import keepmealive.Node;
import keepmealive.TimestepValuePair;
import keepmealive.Utilities;

@NodeEntity
public class Stomach extends Node {

	@Property
	private String type;

	private static final String TYPE_OUTPUT_1 = "output_1";
	private static final String TYPE_OUTPUT_2 = "output_2";
	private static final String TYPE_OUTPUT_3 = "output_3";
	private static final String TYPE_OUTPUT_4 = "output_4";
	private static final String TYPE_INPUT_1 = "input_1";

	private static LimitedStack<TimestepValuePair<Integer>> percentageFull = new LimitedStack<>();

	private static final long REFRACTORY_PERIOD = Constants.ONE_SECOND;

//	public Map<String, String> compute(long timestep) {
//
//		Map<String, String> result = new HashMap<>();
//
//		switch (type) {
//		case TYPE_OUTPUT_1:
//			if (getCurrentValue(timestep) < 50) {
//				getFiredSupersteps().pushItem(timestep);
//			}
//			break;
//		case TYPE_OUTPUT_2:
//			if (getCurrentValue(timestep) < 40) {
//				getFiredSupersteps().pushItem(timestep);
//			}
//			break;
//
//		case TYPE_OUTPUT_3:
//			if (getCurrentValue(timestep) < 30) {
//				getFiredSupersteps().pushItem(timestep);
//			}
//			break;
//
//		case TYPE_OUTPUT_4:
//			if (getCurrentValue(timestep) < 20) {
//				getFiredSupersteps().pushItem(timestep);
//			}
//			break;
//
//		case TYPE_INPUT_1:
//
//			if (getFiredSupersteps().isEmpty()) {
//				getFiredSupersteps().pushItem(timestep);
//				int full = 31;
//				percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, full));
//				result.put("Stomach Full", String.valueOf(full));
//			}
//
//			// Fire every second so that updates only happen once per second
//			if (Utilities.hasPassedRefractoryPeriod(timestep,
//					getFiredSupersteps().isEmpty() ? 0 : getFiredSupersteps().getFirst(), REFRACTORY_PERIOD)) {
//				getFiredSupersteps().pushItem(timestep);
//
//				double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);
//
//				if (weightedSum >= FIRING_THRESHOLD) {
//					// Add food
//					int newValue = getLatestValue() + 4;
//					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));
//
//					result.put("Stomach Full", String.valueOf(newValue));
//				}
//
//				else {
//					// Reduce food
//					int newValue = getLatestValue() - 1;
//					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));
//
//					result.put("Stomach Full", String.valueOf(newValue));
//				}
//
//			}
//
//			break;
//
//		default:
//			// Default case if the type is not recognized
//			break;
//		}
//
//		return result;
//
//	}

	private static int getCurrentValue(long timestep) {
		int currentValue = 0;
		long maxSuperstep = Long.MIN_VALUE;

		// Iterate over the percentageFull stack to find the most recent value based on
		// timestep
		ReadWriteLock lock = percentageFull.getLock();
		lock.readLock().lock();
		try {

			for (TimestepValuePair<Integer> pair : percentageFull) {
				long superstep = pair.getSuperstep();

				if (superstep <= timestep && superstep > maxSuperstep) {
					maxSuperstep = superstep;
					currentValue = pair.getValue();
				}
			}
		} finally {
			// Release read lock
			lock.readLock().unlock();
		}

		return currentValue;
	}

	private int getLatestValue() {
		return percentageFull.getFirst().getValue();
	}

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes,
			long currentSuperstep) {
		for (JsonValue jsonValue : jsonArray) {
			System.out.println("jsonValue: " + jsonValue);
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
		}

	}

	public static String getKey() {
		return "energyLevel";
	}
}
