package priv.gitonlie.websocket.configure;

import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Heartbeat implements Runnable {
	
	private Logger log = LoggerFactory.getLogger(Heartbeat.class);
	
	private Session session;
	
	public Heartbeat(Session session) {
		super();
		this.session = session;
	}

	
	public Heartbeat() {
		super();
	}


	@Override
	public void run() {
		log.info("服务端开启发送心跳模式");
		int i = 0;
		while(session.isOpen()) {
			try {
				String uuid = String.format("%04d", i++)+":the websoket heart is exist 3s";
				log.debug(uuid);
				session.getBasicRemote().sendText(uuid);
				Thread.sleep(1000*3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
