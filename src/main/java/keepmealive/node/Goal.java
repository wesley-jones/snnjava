package keepmealive.node;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.NodeEntity;

import keepmealive.Constants;
import keepmealive.Node;
import keepmealive.Utilities;

@NodeEntity
public class Goal extends Node {

	public static final long REFRACTORY_PERIOD = Constants.ONE_SECOND;

	@Override
	public Map<String, String> compute(long timestep) {

//		if there is an incoming spike and the last time I spiked was more than 1 second, fire
		double weightedSum = super.getFiredUpstreamNeuronWeights(timestep);
		if (weightedSum >= FIRING_THRESHOLD && Utilities.hasPassedRefractoryPeriod(timestep,
				firedSupersteps.isEmpty() ? 0 : firedSupersteps.getLast(), REFRACTORY_PERIOD)) {
			firedSupersteps.pushNumber(timestep);
		}
		Map<String, String> result = new ConcurrentHashMap<>();
		result.put("firedSupersteps_" + id, firedSupersteps.toString());

		return result;
	}

}