package keepmealive;

import java.util.Collection;

import org.glassfish.tyrus.server.Server;

import keepmealive.node.Neuron;

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
			Collection<Neuron> neurons = graphLoader.loadNeurons();
			if (neurons.isEmpty()) {
				System.out.println("No neurons found");
			} else {
				System.out.println(neurons.size() + " neurons found");
			}

			// Simulation of time loop aka Superstep
			int numberOfSupersteps = 100000;

			long startTime = System.currentTimeMillis();

//			while (true) {
			for (int run = 0; run < numberOfSupersteps; run++) {
//				long superstepStartTime = System.currentTimeMillis();

				final int currentRun = run;
				neurons.parallelStream().forEach(neuron -> neuron.compute(currentRun));

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
