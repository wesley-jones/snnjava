package keepmealive;

import java.util.concurrent.CopyOnWriteArrayList;

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
	private static volatile long currentSuperstep = 0;
	public static int processedIncomingMessageCounter = 0;
	public static int totalIncomingMessageCounter = 0;
	private static volatile boolean isProcessingMessage = false;

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
			isProcessingMessage = true;
			processedIncomingMessageCounter++;

			// Create a copy so that this does not change during the translation
			long copyOfCurrentSuperstep = currentSuperstep;
			SensoryInputEncoder.translateMessage(message, copyOfCurrentSuperstep);

			// Set the flag to indicate that a message has been processed in this superstep
			processedMessageInSuperstep = true;
			isProcessingMessage = false;
		}
	}

	public static void broadcast(String data) {
		for (Session session : sessions) {
			session.getAsyncRemote().sendText(data);
		}
	}

	// Reset the flag for the next superstep
	public static void resetProcessedMessageFlag(long newSuperstep) {
		processedMessageInSuperstep = false;
		currentSuperstep = newSuperstep + 1;
	}

	public static boolean isProcessingMessage() {
		return isProcessingMessage;
	}
}
