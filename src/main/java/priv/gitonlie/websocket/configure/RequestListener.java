package priv.gitonlie.websocket.configure;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class RequestListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		// TODO Auto-generated method stub
		ServletRequestListener.super.requestDestroyed(sre);
	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		 //将所有request请求都携带上httpSession
		HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
		req.getSession();
//        ((HttpServletRequest) sre.getServletRequest()).getSession();
	}
	
	
}
