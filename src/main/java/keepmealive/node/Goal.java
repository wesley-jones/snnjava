package keepmealive.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.NodeEntity;

import keepmealive.Node;

@NodeEntity
public class Goal extends Node {

	private static final String GOAL_WALKING = "Walking";

	private static final String GOAL_EATING = "Eating";

	// Only used for communication with the UI
	private static String goal = GOAL_WALKING;

	@Override
	public Map<String, String> compute(long timestep) {

		Map<String, String> result = new ConcurrentHashMap<>();
		if (timestep == 0) {
			result.put("Goal", goal);
		}

		double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

		System.out.println("Weighted sum: " + weightedSum);

		if (weightedSum >= FIRING_THRESHOLD) {
			System.out.println("Goal - Eating - Fired");
			firedSupersteps.pushItem(timestep);
			if (goal != GOAL_EATING) {
				goal = GOAL_EATING;
				result.put("Goal", goal);
			}
		} else {
			if (goal != GOAL_WALKING) {
				goal = GOAL_WALKING;
				result.put("Goal", goal);
			}
		}

		return result;
	}
}
