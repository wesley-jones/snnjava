package keepmealive.relationship;

import org.neo4j.ogm.annotation.*;

import keepmealive.node.Neuron;

@RelationshipEntity(type = "CONNECTS_TO")
public class ConnectsTo {

	@Id
	private Long id;

	@StartNode
	private Neuron neuron1;

	@EndNode
	private Neuron neuron2;

	@Property
	private int weight;

}