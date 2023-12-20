package keepmealive.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.NodeEntity;

import keepmealive.Node;

@NodeEntity
public class Goal extends Node {

	@Override
	public Map<String, String> compute(long timestep) {
		Map<String, String> result = new ConcurrentHashMap<>();
		double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);

		if (weightedSum >= FIRING_THRESHOLD) {
			firedSupersteps.pushItem(timestep);
			result.put("Eating Status", "Eating");
		} else {
//			result.put("Eating Status", "Not Eating");
		}

		return result;
	}
}
