package keepmealive.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import keepmealive.Constants;
import keepmealive.LimitedStack;
import keepmealive.Node;
import keepmealive.TimestepValuePair;
import keepmealive.Utilities;

@NodeEntity
public class Stomach extends Node {

	@Property
	private String type;

	public static final String TYPE_OUTPUT_1 = "output_1";
	public static final String TYPE_OUTPUT_2 = "output_2";
	public static final String TYPE_OUTPUT_3 = "output_3";
	public static final String TYPE_OUTPUT_4 = "output_4";
	public static final String TYPE_INPUT_1 = "input_1";

	private static LimitedStack<TimestepValuePair<Integer>> percentageFull = new LimitedStack<>();

	public static final long REFRACTORY_PERIOD = Constants.ONE_SECOND;

	public Map<String, String> compute(long timestep) {

		Map<String, String> result = new ConcurrentHashMap<>();

		switch (type) {
		case TYPE_OUTPUT_1:
			// Logic for TYPE_OUTPUT_1
			result.put("output_1_result", "Result for output_1");
			break;
		case TYPE_OUTPUT_2:
			// Logic for TYPE_OUTPUT_2
			result.put("output_2_result", "Result for output_2");
			break;

		case TYPE_OUTPUT_3:
			// Logic for TYPE_OUTPUT_2
			result.put("output_2_result", "Result for output_2");
			break;

		case TYPE_OUTPUT_4:
			// Logic for TYPE_OUTPUT_2
			result.put("output_2_result", "Result for output_2");
			break;

		case TYPE_INPUT_1:

			// Fire every second so that updates only happen once per second
			if (Utilities.hasPassedRefractoryPeriod(timestep, firedSupersteps.isEmpty() ? 0 : firedSupersteps.getLast(),
					REFRACTORY_PERIOD)) {
				firedSupersteps.pushItem(timestep);

				double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

				if (weightedSum >= FIRING_THRESHOLD) {
					// Add food
					int value = percentageFull.getLast().getValue();
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, value + 1));

					result.put("Stomach Full", "Eating");
				}

				else {
					// Reduce food
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, 10));

					result.put("Stomach Full", "Eating");
				}

			}

			break;

		default:
			// Default case if the type is not recognized
			break;
		}

		return result;

		/*
		 * The stomach sends signals to its connected neurons. The stomach increases
		 * signals as it gets depleted. At 50% one node starts signaling. At 40% two
		 * nodes start signaling. At 30% three nodes start signaling. At 20% four nodes
		 * start signaling.
		 * 
		 * Incoming food increases the percentage full.
		 */

//		List<ConnectsTo> upstreamRelationships = getUpstreamRelationships();

//		for (ConnectsTo relationship : upstreamRelationships) {
//			Node upstreamNode = relationship.getIncomingNode();
//			if (upstreamNode.getClass() == Goal.class) {
//				
//			}
//			if (upstreamNode.getFiredSupersteps().contains(timestep - 1)) {
//				
//			}
//		}

//		return null;

	}
}
