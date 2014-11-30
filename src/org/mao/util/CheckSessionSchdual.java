package org.mao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.websocket.Session;

public class CheckSessionSchdual extends TimerTask {
	public static SessionContainer session;

	public CheckSessionSchdual(SessionContainer session) {
		CheckSessionSchdual.session = session;
	}

	@Override
	public void run() {
		List<String> dieIds = new ArrayList<String>();
		/*if (session.keySet().size() > 2) {
			for (String id : session.keySet()) {
				try {
					session.get(id).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}*/
		for (String id : session.keySet()) {
			Session sess = session.get(id);
			if (!sess.isOpen()) {
				dieIds.add(id);
			}
		}
		for (String id : dieIds) {
			session.remove(id);
		}
	}
}
