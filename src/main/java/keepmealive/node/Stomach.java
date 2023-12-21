package keepmealive.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

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

	private static final String TYPE_OUTPUT_1 = "output_1";
	private static final String TYPE_OUTPUT_2 = "output_2";
	private static final String TYPE_OUTPUT_3 = "output_3";
	private static final String TYPE_OUTPUT_4 = "output_4";
	private static final String TYPE_INPUT_1 = "input_1";

	private static LimitedStack<TimestepValuePair<Integer>> percentageFull = new LimitedStack<>();

	private static final long REFRACTORY_PERIOD = Constants.ONE_SECOND;

	public Map<String, String> compute(long timestep) {

		Map<String, String> result = new ConcurrentHashMap<>();

		switch (type) {
		case TYPE_OUTPUT_1:
			if (getCurrentValue(timestep) < 50) {
				System.out.println("Hungry 1 - Fired");
				firedSupersteps.pushItem(timestep);
			}
			break;
		case TYPE_OUTPUT_2:
			if (getCurrentValue(timestep) < 40) {
				System.out.println("Hungry 2 - Fired");
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_OUTPUT_3:
			if (getCurrentValue(timestep) < 30) {
				System.out.println("Hungry 3 - Fired");
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_OUTPUT_4:
			if (getCurrentValue(timestep) < 20) {
				System.out.println("Hungry 4 - Fired");
				firedSupersteps.pushItem(timestep);
			}
			break;

		case TYPE_INPUT_1:

			if (firedSupersteps.isEmpty()) {
				firedSupersteps.pushItem(timestep);
				int full = 31;
				percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, full));
				result.put("Stomach Full", String.valueOf(full));
			}
			System.out.println("fullness: " + getLatestValue());

			// Fire every second so that updates only happen once per second
			if (Utilities.hasPassedRefractoryPeriod(timestep,
					firedSupersteps.isEmpty() ? 0 : firedSupersteps.getFirst(), REFRACTORY_PERIOD)) {
				firedSupersteps.pushItem(timestep);

				System.out.println("Inner Stomach node initiated");

				double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

				if (weightedSum >= FIRING_THRESHOLD) {
					System.out.println("Goal - Eating - Received by Stomach");
					// Add food
					int newValue = getLatestValue() + 4;
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));

					result.put("Stomach Full", String.valueOf(newValue));
				}

				else {
					System.out.println("Goal - Eating - Not received by Stomach");
					// Reduce food
					int newValue = getLatestValue() - 1;
					percentageFull.pushItem(new TimestepValuePair<Integer>(timestep, newValue));

					result.put("Stomach Full", String.valueOf(newValue));
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
}
