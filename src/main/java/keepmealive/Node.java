package keepmealive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import keepmealive.relationship.ConnectsTo;

public abstract class Node implements Computable {

	@Id
	protected Long id;

	@Relationship(type = "ConnectsTo", direction = Relationship.Direction.INCOMING)
	private List<ConnectsTo> upstreamRelationships = new ArrayList<ConnectsTo>();

	protected LimitedStack firedSupersteps = new LimitedStack();

	public static final int FIRING_THRESHOLD = 1;

	private LimitedStack getFiredSupersteps() {
		return firedSupersteps;
	}

	protected List<ConnectsTo> getUpstreamRelationships() {
		return upstreamRelationships;
	}

	@Override
	public Map<String, String> compute(long timestep) {
		double weightedSum = getFiredUpstreamNeuronWeights(timestep);
		if (weightedSum >= FIRING_THRESHOLD) {
			firedSupersteps.pushNumber(timestep);
		}
		Map<String, String> result = new ConcurrentHashMap<>();
		result.put("firedSupersteps_" + id, firedSupersteps.toString());

		return result;
	}

	protected double getFiredUpstreamNeuronWeights(long timestep) {
		List<ConnectsTo> relationships = getUpstreamRelationships();
		double weightedSum = 0;

		// Iterate over ConnectsTo relationships to get the upstream Neurons
		for (ConnectsTo relationship : relationships) {
			Node upstreamNeuron = relationship.getIncomingNode();
			if (upstreamNeuron.getFiredSupersteps().contains(timestep - 1)) {
				weightedSum += relationship.getWeight();
			}
		}
		return weightedSum;
	}
}
