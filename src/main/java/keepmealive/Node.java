package keepmealive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import jakarta.json.JsonArray;
import keepmealive.relationship.ConnectsTo;

public abstract class Node extends KeyProvider implements Computable {

	@Id
	protected Long id;

	@Relationship(type = "ConnectsTo", direction = Relationship.Direction.INCOMING)
	private List<ConnectsTo> upstreamRelationships = new ArrayList<ConnectsTo>();

	private LimitedStack<Long> firedSupersteps = new LimitedStack<Long>();

	public static final int FIRING_THRESHOLD = 1;

	private double voltage;

	// Rate at which voltage decreases over time
	private static final double DECAY_RATE = 10;

	public LimitedStack<Long> getFiredSupersteps() {
		return firedSupersteps;
	}

	protected List<ConnectsTo> getUpstreamRelationships() {
		return upstreamRelationships;
	}

	@Override
	public Map<String, String> compute(long timestep) {

		double weightedSum = getFiredUpstreamNeuronWeights(timestep);

		// Update voltage based on the weighted sum
		updateVoltage(weightedSum);

		// Check if voltage meets the firing threshold
		if (voltage >= FIRING_THRESHOLD) {
			firedSupersteps.pushItem(timestep);
			voltage = 0;
		}

		Map<String, String> result = new HashMap<>();

		return result;
	}

	private void updateVoltage(double weightedSum) {
		// Decrease voltage over time (decay)
		voltage = voltage * Math.exp(-DECAY_RATE);

		// Increase voltage based on the weighted sum
		voltage += weightedSum;
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

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes,
			long currentSuperstep) {
		throw new UnsupportedOperationException("translateInput method not implemented");
	}

	public static String getKey() {
		// Return the class name
		// Example:
		// return Node.class.getSimpleName().toLowerCase();
		throw new UnsupportedOperationException("getKey method not implemented");
	}

	public Long getId() {
		return id;
	}

}
