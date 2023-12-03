package keepmealive;

import org.glassfish.tyrus.server.Server;

public class WebSocketServer {

	public static void main(String[] args) {
		
		Server server = new Server("localhost", 8025, "/websocket", null, SnnWebSocket.class);

		try {
			server.start();
			System.out.println("WebSocket server started. Press Ctrl+C to stop.");
			
			// Load the graph data
			// TODO: Consider compacting the graph data into a linked list or edge list to reduce memory footprint
			

			// SNN simulation loop
			while (true) {
				// Simulate SNN and get data
				double x = Math.random() * 400; // Example data (replace with your SNN logic)
				double y = Math.random() * 400;

				// Broadcast data to connected clients
				SnnWebSocket.broadcast("{\"x\":" + x + ", \"y\":" + y + "}");

				// Delay for a short time (adjust as needed)
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}
}
