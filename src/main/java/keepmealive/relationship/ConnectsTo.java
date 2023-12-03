package keepmealive.relationship;

import org.neo4j.ogm.annotation.*;

import keepmealive.node.Neuron;

@RelationshipEntity(type = "CONNECTS_TO")
public class ConnectsTo {

	@Id
	private Long id;

	@StartNode
	private Neuron incomingNeuron;

	@EndNode
	private Neuron outgoingNeuron;

	@Property
	private double weight;

	public Neuron getIncomingNeuron() {
		return incomingNeuron;
	}

	public double getWeight() {
		return weight;
	}

}
