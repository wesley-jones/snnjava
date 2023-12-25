package keepmealive;

import java.io.StringReader;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/snn")
public class SnnWebSocket {

	private static final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();
	private static volatile boolean processedMessageInSuperstep = false;
	public static int processedIncomingMessageCounter = 0;
	public static int totalIncomingMessageCounter = 0;

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Client connected: " + session.getId());
		sessions.add(session);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Client disconnected: " + session.getId());
		sessions.remove(session);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		totalIncomingMessageCounter++;
		if (!processedMessageInSuperstep) {
			processedIncomingMessageCounter++;
			// Store the message only if it hasn't been processed in the current superstep
			processMessage(message);

			// Set the flag to indicate that a message has been processed in this superstep
			processedMessageInSuperstep = true;
		}
	}

	public static void broadcast(String data) {
		for (Session session : sessions) {
			session.getAsyncRemote().sendText(data);
		}
	}

	// Reset the flag for the next superstep
	public static void resetProcessedMessageFlag() {
		processedMessageInSuperstep = false;
	}

	public static void processMessage(String message) {
		// Check if storedMessage is not an empty string
		if (!message.isEmpty()) {

			try {
				// Parse JSON message
				JsonObject jsonObject;
				try (JsonReader jsonReader = Json.createReader(new StringReader(message))) {
					jsonObject = jsonReader.readObject();
				}

				// Access the values in the JsonObject
				if (jsonObject.containsKey("Stomach Full")) {
					String stomachFullValue = jsonObject.getString("Stomach Full");
					System.out.println("Stomach Full value: " + stomachFullValue);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
