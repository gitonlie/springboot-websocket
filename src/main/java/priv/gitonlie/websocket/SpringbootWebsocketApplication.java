package priv.gitonlie.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

import priv.gitonlie.websocket.configure.RequestListener;

@SpringBootApplication
public class SpringbootWebsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebsocketApplication.class, args);
    }
    
    @Autowired
    private RequestListener requestListener;
  
    @Bean
    public ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean() {
         ServletListenerRegistrationBean<RequestListener> servletListenerRegistrationBean = new ServletListenerRegistrationBean<>();
         servletListenerRegistrationBean.setListener(requestListener);
         return servletListenerRegistrationBean;
    }

}
