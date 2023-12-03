package keepmealive.node;

import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import keepmealive.LimitedStack;
import keepmealive.relationship.ConnectsTo;

@NodeEntity
public class Neuron {

	@Id
	private Long id;

	@Relationship(type = "ConnectsTo", direction = Relationship.Direction.INCOMING)
	private List<ConnectsTo> upstreamRelationships = new ArrayList<ConnectsTo>();

	private static final int FIRING_THRESHOLD = 1;

	private LimitedStack firedSupersteps = new LimitedStack();

	public Long getId() {
		return id;
	}

	private List<ConnectsTo> getUpstreamRelationships() {
		return upstreamRelationships;
	}

	public void compute(long timestep) {
		double weightedSum = getFiredUpstreamNeuronWeights(timestep);
		if (weightedSum >= FIRING_THRESHOLD || timestep == 0) {
			firedSupersteps.pushNumber(timestep);
		}
	}

	private double getFiredUpstreamNeuronWeights(long timestep) {
		List<ConnectsTo> relationships = getUpstreamRelationships();
		double weightedSum = 0;

		// Iterate over ConnectsTo relationships to get the upstream Neurons
		for (ConnectsTo relationship : relationships) {
			Neuron upstreamNeuron = relationship.getIncomingNeuron();
			if (upstreamNeuron.firedSupersteps.contains(timestep - 1)) {
				weightedSum += relationship.getWeight();
			}
		}
		return weightedSum;

	}
}
