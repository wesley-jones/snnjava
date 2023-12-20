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
			if (getCurrentValue(timestep) < 50) {
				firedSupersteps.pushItem(timestep);
			}
			break;
		case TYPE_OUTPUT_2:
			if (getCurrentValue(timestep) < 40) {
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_OUTPUT_3:
			if (getCurrentValue(timestep) < 30) {
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_OUTPUT_4:
			if (getCurrentValue(timestep) < 20) {
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_INPUT_1:

			if (firedSupersteps.isEmpty()) {
				firedSupersteps.pushItem(timestep);
				int full = 100;
				percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, full));
				result.put("Stomach Full", String.valueOf(full));
			}
			System.out.println("fullness: " + getLatestValue());

			// Fire every second so that updates only happen once per second
			if (Utilities.hasPassedRefractoryPeriod(timestep,
					firedSupersteps.isEmpty() ? 0 : firedSupersteps.getFirst(), REFRACTORY_PERIOD)) {
				firedSupersteps.pushItem(timestep);

				double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

				if (weightedSum >= FIRING_THRESHOLD) {
					// Add food
					int newValue = getLatestValue() + 4;
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));

					result.put("Stomach Full", String.valueOf(newValue));
				}

				else {
					// Reduce food
					int newValue = getLatestValue() - 1;
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));

					result.put("Stomach Full", "Eating");
				}

			}

			break;

		default:
			// Default case if the type is not recognized
			break;
		}

		return result;

	}

	private static int getCurrentValue(long timestep) {
		int currentValue = 0;
		long maxSuperstep = Long.MIN_VALUE;

		// Iterate over the percentageFull stack to find the most recent value based on
		// timestep
		for (TimestepValuePair<Integer> pair : percentageFull) {
			long superstep = pair.getSuperstep();

			if (superstep <= timestep && superstep > maxSuperstep) {
				maxSuperstep = superstep;
				currentValue = pair.getValue();
			}
		}

		return currentValue;
	}

	private int getLatestValue() {
		return percentageFull.getFirst().getValue();
	}
}
