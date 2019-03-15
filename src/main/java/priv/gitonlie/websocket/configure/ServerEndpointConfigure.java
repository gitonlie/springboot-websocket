package priv.gitonlie.websocket.configure;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
@ServerEndpoint(value="/websocket",configurator=HttpSessionConfigurator.class)
public class ServerEndpointConfigure {
		
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<ServerEndpointConfigure> webSocketSet = new CopyOnWriteArraySet<ServerEndpointConfigure>();
    //若要实现服务端与指定客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    public static ConcurrentHashMap<Session,Object> webSocketMap = new ConcurrentHashMap<Session,Object>();
    //绑定token与session
    public static ConcurrentHashMap<String,Session> bizSession = new ConcurrentHashMap<String,Session>();
	//与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
 
    /**
     * 连接建立成功调用的方法
     * @throws IOException 
     **/
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) throws IOException {
        this.session = session;
        Map<String, Object> userProperties = config.getUserProperties();
        HttpSession httpSession = (HttpSession) userProperties.get(HttpSession.class.getName());
        log.info("httpSession:{},websocketSession:{}----",httpSession.getId(),session.getId());        
        bizSession.put(httpSession.getId(), session);//建立关联
        webSocketMap.put(session, this);
        webSocketSet.add(this);//加入set中
        addOnlineCount();//在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
        	 sendMessage("["+httpSession.getId()+"]连接成功",session);
        	 sendMessage("sessionId:"+httpSession.getId(),session);
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session closeSession,CloseReason reason) {
    	for(String key:bizSession.keySet()){
    		Session session = bizSession.get(key);
    		if(session.equals(closeSession)){
    			bizSession.remove(key);
    		}
    	}   	
    	webSocketMap.remove(closeSession);
        webSocketSet.remove(closeSession);  //从set中删除
        subOnlineCount();           //在线数减1
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session mySession) {
    	log.info("来自客户端的消息:" + message);

    	//推送给单个客户端
		for (Session session : webSocketMap.keySet()) {
			if (session.equals(mySession)) {
				ServerEndpointConfigure item = (ServerEndpointConfigure) webSocketMap.get(mySession);
				try {
					item.sendMessage(message,session);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
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

    
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
    	ServerEndpointConfigure.onlineCount++;
    }
 
    public static synchronized void subOnlineCount() {
    	ServerEndpointConfigure.onlineCount--;
    }
}
