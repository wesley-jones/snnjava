package keepmealive;

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
			NodeRegistry nodeRegistry = NodeRegistry.getInstance();
			Collection<Node> nodes = nodeRegistry.getAllLoadedNodes();

			if (nodes.isEmpty()) {
				System.out.println("No nodes found");
			} else {
				System.out.println(nodes.size() + " nodes found");
			}

			// Simulation of time loop aka Superstep
			int numberOfSupersteps = (int) (Constants.ONE_SECOND * 20000);
			long startTime = System.currentTimeMillis();
			for (int run = 0; run < numberOfSupersteps; run++) {
				final int currentRun = run;

				// Use parallelStream to process Computables concurrently
				// Manage threads outside of loop if you need this to be faster.
				Map<String, String> resultCollector = nodes.parallelStream()
						.map(computable -> computable.compute(currentRun)).filter(result -> !result.isEmpty())
						.flatMap(map -> map.entrySet().stream())
						.collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1));

				if (!resultCollector.isEmpty()) {
					// Convert Map to JSON-like string
					String jsonLikeString = Utilities.convertMapToJson(resultCollector);
					System.out.println("outbound: " + jsonLikeString);
					SnnWebSocket.broadcast(jsonLikeString);
				}
				// Wait until isProcessingMessage is false before moving to the next iteration
				while (SnnWebSocket.isProcessingMessage()) {
					try {
						Thread.sleep(1); // You can adjust the sleep duration based on your needs
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				SnnWebSocket.resetProcessedMessageFlag(run);
			}
			long endTime = System.currentTimeMillis();
			long elapsedTime = endTime - startTime;
			double averageIterations = (double) elapsedTime / numberOfSupersteps;
			int unprocessedMessages = SnnWebSocket.totalIncomingMessageCounter
					- SnnWebSocket.processedIncomingMessageCounter;
			double percentageUnprocessed = ((double) unprocessedMessages / SnnWebSocket.totalIncomingMessageCounter)
					* 100;

			System.out.println("Average superstep: " + averageIterations + " milliseconds");
			System.out.println("Total Incoming Messages: " + SnnWebSocket.totalIncomingMessageCounter);
			System.out.println("Processed Incoming Messages: " + SnnWebSocket.processedIncomingMessageCounter);
			System.out.println("Ignored Incoming Messages: " + unprocessedMessages);
			System.out.printf("Percentage of Ignored Messages: %.2f%%\n", percentageUnprocessed);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}

	}
}
