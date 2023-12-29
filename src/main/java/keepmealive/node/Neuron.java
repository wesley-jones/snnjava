package keepmealive.node;

import java.util.Collection;

import org.neo4j.ogm.annotation.NodeEntity;

import jakarta.json.JsonArray;
import keepmealive.Node;

@NodeEntity
public class Neuron extends Node {

	public static void translateInput(JsonArray jsonArray, Collection<? extends Node> loadedNodes) {
		// Does nothing

	}

	public static String getKey() {
		return "neuron";
	}

}
