package keepmealive.node;

import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;

import keepmealive.Constants;
import keepmealive.LimitedStack;
import keepmealive.Node;
import keepmealive.relationship.ConnectsTo;

@NodeEntity
public class Stomach extends Node {

	private static int percentageFull = 100;

	private long lastDecrementSuperstep = 0;

	public Long getId() {
		return id;
	}

	public Map<String, String> compute(long timestep) {

		/*
		 * The stomach sends signals to its connected neurons. The stomach increases
		 * signals as it gets depleted. At 50% one node starts signaling. At 40% two
		 * nodes start signaling. At 30% three nodes start signaling. At 20% four nodes
		 * start signaling.
		 * 
		 * Incoming food increases the percentage full.
		 */

		List<ConnectsTo> upstreamRelationships = getUpstreamRelationships();

//		for (ConnectsTo relationship : upstreamRelationships) {
//			Node upstreamNode = relationship.getIncomingNode();
//			if (upstreamNode.getClass() == Goal.class) {
//				
//			}
//			if (upstreamNode.getFiredSupersteps().contains(timestep - 1)) {
//				
//			}
//		}

		if ((timestep - Constants.ONE_SECOND) > lastDecrementSuperstep) {
			percentageFull -= 1;
			lastDecrementSuperstep = timestep;
		}
		return null;

	}

	public LimitedStack getFiredSupersteps() {
		return firedSupersteps;
	}
}
