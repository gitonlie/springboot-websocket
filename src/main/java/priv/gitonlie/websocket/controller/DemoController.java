package priv.gitonlie.websocket.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import priv.gitonlie.websocket.configure.PropertiesConfiguration;
import priv.gitonlie.websocket.configure.ServerEndpointConfigure;
import priv.gitonlie.websocket.util.QRCodeUtil;

@Controller
@Slf4j
public class DemoController {
	
	@Autowired
	private PropertiesConfiguration configuration;
	@Autowired
	private ServerEndpointConfigure server;
	
    @GetMapping("demo")
    @ResponseBody
    public String demo() {
        return "this is a demo~";
    }
    
    @RequestMapping("/")
    public String index() {
		return "index";   	
    }
    
    @RequestMapping("/qrcode")
    public void qrcode(HttpServletRequest request,HttpServletResponse response) throws Exception {
    	String sessionId = request.getParameter("sessionId");
    	String uuid = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    	String path = configuration.getNet()+"/flush?sessionId="+sessionId+"&"+uuid;
    	log.info("qrcode:{}",path);
		QRCodeUtil.generateQRCode(path, 200, 200, "png", response);
    }
    
    //移动端信息
    @RequestMapping("flush")
    public String flush(String sessionId,Model model,HttpServletRequest request) {
    	model.addAttribute("sessionId", sessionId);
    	log.info("flush:{}",sessionId);
		return "mobile";   	
    }
    
    @RequestMapping("sm")
    @ResponseBody
    public Map<String,Object> sm(HttpServletRequest request,HttpServletResponse response) throws IOException {
    	Map<String,Object> result = new HashMap<String, Object>();
    	String sessionId = request.getParameter("sessionId");
    	log.info("sm:{}",sessionId);
    	result.put("code", "0000");
    	result.put("sessionId", sessionId);
    	//向websocket发送消息
    	Session session = ServerEndpointConfigure.bizSession.get(sessionId);
    	server.sendMessage("二维码已刷新", session);
		return result;
    }
}
