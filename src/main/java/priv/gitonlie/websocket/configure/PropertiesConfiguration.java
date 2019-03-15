package priv.gitonlie.websocket.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PropertiesConfiguration {
	
	@Value("${server.url}")
	private String net;

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}
	
}
