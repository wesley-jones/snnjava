package keepmealive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.glassfish.tyrus.server.Server;

public class WebSocketServer {

	public static void main(String[] args) {

		Server server = new Server("localhost", 8025, "/websocket", null, SnnWebSocket.class);

		try {
			server.start();
			System.out.println("WebSocket server started. Press Ctrl+C to stop.");

			// Load the environment objects
			// TODO: Consider compacting the graph data into a linked list or edge list to
			// reduce memory footprint
			GraphLoader graphLoader = new GraphLoader();
			Collection<Node> nodes = new ArrayList<>(graphLoader.loadNeurons());
//			nodes.addAll(graphLoader.loadStomach());
			nodes.addAll(graphLoader.loadGoals());

			if (nodes.isEmpty()) {
				System.out.println("No nodes found");
			} else {
				System.out.println(nodes.size() + " nodes found");
			}

			// Simulation of time loop aka Superstep
			int numberOfSupersteps = 4;

			long startTime = System.currentTimeMillis();

//			while (true) {
			for (int run = 0; run < numberOfSupersteps; run++) {
//				long superstepStartTime = System.currentTimeMillis();

				final int currentRun = run;
//				nodes.parallelStream().forEach(neuron -> neuron.compute(currentRun));

				// Use parallelStream to process Computables concurrently
				Map<String, String> resultCollector = nodes.parallelStream()
						.map(computable -> computable.compute(currentRun)).filter(result -> !result.isEmpty())
						.flatMap(map -> map.entrySet().stream())
						.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

				System.out.println("resultCollector: " + resultCollector);

				// Simulate SNN and get data
				double x = Math.random() * 400; // Example data (replace with your SNN logic)
				double y = Math.random() * 400;

				// Broadcast data to connected clients
				SnnWebSocket.broadcast("{\"x\":" + x + ", \"y\":" + y + "}");

				// Delay for a short time (adjust as needed)
//				Thread.sleep(1000);

//				long superstepEndTime = System.currentTimeMillis();
//				long superstepElapsedTime = superstepEndTime - superstepStartTime;

//				System.out.println("Run " + (run + 1) + " in " + superstepElapsedTime + " milliseconds");

			}
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			double averageIterations = (double) elapsedTime / numberOfSupersteps;
			System.out.println("Average superstep: " + averageIterations + " milliseconds");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}
}
