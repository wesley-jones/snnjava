package keepmealive.relationship;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import keepmealive.Node;

@RelationshipEntity(type = "CONNECTS_TO")
public class ConnectsTo {

	@Id
	private Long id;

	@StartNode
	private Node incomingNode;

	@EndNode
	private Node outgoingNode;

	@Property
	private double weight;

	public Node getIncomingNode() {
		return incomingNode;
	}

	public double getWeight() {
		return weight;
	}

}
