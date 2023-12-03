package keepmealive.node;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Id;

@NodeEntity
public class Neuron {

	@Id
	private Long id;
	
	public Long getId() {
		return id;
	}

}