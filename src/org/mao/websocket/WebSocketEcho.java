package org.mao.websocket;

import java.util.ArrayList;
import java.util.Timer;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;
import org.mao.util.CheckSessionSchdual;
import org.mao.util.SessionContainer;

@ServerEndpoint("/echo")
public class WebSocketEcho {

	private static SessionContainer sessions = new SessionContainer();
	private static Integer checkFlag = 0;

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			if (message.indexOf(",") != -1) {
				String code = message.substring(0, message.indexOf(","));
				String data = message.substring(message.indexOf(",") + 1,
						message.length());
				switch (code) {
				case "1":
					session.getBasicRemote().sendText(getAllUsers());
					break;
				case "2":
					if (data.indexOf(",") != -1) {
						String sessionId = data.substring(0, data.indexOf(","));
						String text = data.substring(data.indexOf(",") + 1,
								data.length());
						Session desSession = sessions.get(sessionId);
						if (null != desSession && desSession.isOpen()) {
							desSession.getBasicRemote().sendText(
									formatMessage(2, text, session.getId()));
						} else {
							session.getBasicRemote().sendText(
									formatMessage(3, "对方可能不在线了，抱歉", ""));
						}
					}
					break;
				case "3":
					break;
				default:
					break;
				}
			} else {
				System.err.println("接收到了非法send数据 :" + message);
			}
		} catch (Exception e) {
			System.err.println("exception in onMessage :" + e.getMessage());
		}
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig config) {
		try {
			String id = session.getId();
			if (checkFlag == 0) {
				checkAliveSession();
				checkFlag = 1;
			}
			sessions.put(id, session);
			session.getBasicRemote().sendText(formatMessage(1, id, ""));
			System.out.println("新连接创建成功");
		} catch (Exception e) {
			System.err.println("exception in onOpen :" + e.getMessage());
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		try {
			System.out.println("Connection closed");
			session.getBasicRemote().sendText("session closed");
		} catch (Exception e) {
			System.err.println("exception in onClose :" + e.getMessage());
		}
	}

	@OnError
	public void onError(Throwable error, Session session) {
		try {
			System.out.println("Connection error");
			session.getBasicRemote().sendText("session error");
		} catch (Exception e) {
			System.err.println("exception in onError :" + e.getMessage());
		}
	}

	private String getAllUsers() {
		JSONObject json = new JSONObject();
		try {
			json.put("data", new ArrayList<String>(sessions.keySet()));
			json.put("status", 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	private String formatMessage(int type, String message, String desId) {
		JSONObject json = new JSONObject();
		try {
			json.put("status", type);
			json.put("data", message);
			json.put("desId", desId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	private void checkAliveSession() {
		Timer timer = new Timer();
		CheckSessionSchdual task = new CheckSessionSchdual(sessions);
		timer.schedule(task, 0, 10000);
	}
}
