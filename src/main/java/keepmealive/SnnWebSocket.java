package keepmealive;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/snn")
public class SnnWebSocket {

	private static final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();

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
		// Handle incoming messages if needed
	}

	public static void broadcast(String data) {
		for (Session session : sessions) {
			Future<Void> future = session.getAsyncRemote().sendText(data);
//			try {
//				session.getBasicRemote().sendText(data);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
}
