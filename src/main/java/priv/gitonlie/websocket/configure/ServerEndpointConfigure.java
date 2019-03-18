package priv.gitonlie.websocket.configure;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint(value="/websocket/{sid}",configurator=HttpSessionConfigurator.class)
public class ServerEndpointConfigure {

	//设定原子整型,用来记录当前在线连接数
    private AtomicInteger onlineCount = new AtomicInteger(0);
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    private static CopyOnWriteArraySet<ServerEndpointConfigure> webSocketSet = new CopyOnWriteArraySet<ServerEndpointConfigure>();
    //若要实现服务端与指定客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
//    public static ConcurrentHashMap<Session,Object> webSocketMap = new ConcurrentHashMap<Session,Object>();
    
    //绑定HttpSession与session
    public static ConcurrentHashMap<String,Session> bizSession = new ConcurrentHashMap<String,Session>();
	//与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
 
    /**
     * <p>连接建立成功调用的方法</p>
     * @throws IOException 
     **/
    @OnOpen
    public void onOpen(Session session,EndpointConfig config,@PathParam("sid") String sid) throws IOException {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        log.info("httpSessionId:{},websocketSessionId:{},sid:{}----",httpSession.getId(),session.getId(),sid);        
        bizSession.put(httpSession.getId(), session);//建立关联
        addOnlineCount();//在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
        	 sendMessage("["+httpSession.getId()+"]连接成功",session);
        	 //设定模拟线程
        	 new Thread(new Heartbeat(session)).start();
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }
    
    /**
     * <p>连接关闭调用的方法</p>
     */
    @OnClose
    public void onClose(Session closeSession,CloseReason reason) {
    	log.info(reason.toString());
    	for(String key:bizSession.keySet()){
    		Session session = bizSession.get(key);
    		if(session.equals(closeSession)){
    			bizSession.remove(key);
    		}
    	}   	
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * <p>收到客户端消息后调用的方法</p>
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session mySession) {
    	log.info("来自客户端的消息:" + message);
    	try {
			sendMessage(message, mySession);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
	/**
	 * 
	 * @param session
	 * @param error
	 */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        for(String key:bizSession.keySet()){
    		Session sessionc = bizSession.get(key);
    		if(session.equals(sessionc)){
    			bizSession.remove(key);
    		}
    	} 
        error.printStackTrace();
    }
       
 
    public void sendMessage(String message,Session session) throws IOException {   	
    	try {
			if(session.isOpen()){//该session如果已被删除，则不执行发送请求，防止报错
				session.getBasicRemote().sendText(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}   	
    }

    
    public int getOnlineCount() {//获取当前值
		return onlineCount.get();   	
    }
    
    public int addOnlineCount() {//加1
    	return onlineCount.getAndIncrement();
    }
   
    public int subOnlineCount() {//减1
    	return onlineCount.getAndDecrement();
    }
}
