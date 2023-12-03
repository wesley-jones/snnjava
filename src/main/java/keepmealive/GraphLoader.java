package keepmealive;

import java.util.Collection;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import keepmealive.node.Neuron;

public class GraphLoader {

	private final Session session;

	public GraphLoader() {
		System.out.println("Loading Neo4j config");
		Configuration configuration = new Configuration.Builder().uri("bolt://localhost:7687") // Neo4j URI
				.credentials("neo4j", getPassword()) // Neo4j credentials
				.withBasePackages("keepmealive.node", "keepmealive.relationship")
				.build();
		
		System.out.println("Starting Neo4j SessionFactory");

		SessionFactory sessionFactory = new SessionFactory(configuration);
		this.session = sessionFactory.openSession();
	}

	private String getPassword() {
		String neo4jPassword = System.getenv("NEO4J_PASSWORD");

		if (neo4jPassword == null || neo4jPassword.isEmpty()) {
			throw new RuntimeException("Neo4j password not set.");
		}

		return neo4jPassword;
	}

	public Collection<Neuron> loadNeurons() {
		System.out.println("Querying for all Neurons");
		return session.loadAll(Neuron.class);
	}
}
