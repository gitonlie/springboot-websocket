package priv.gitonlie.websocket.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import priv.gitonlie.websocket.configure.PropertiesConfiguration;
import priv.gitonlie.websocket.configure.ServerEndpointConfigure;

@Controller
@Slf4j
public class DemoController {
	
    @GetMapping("demo")
    @ResponseBody
    public String demo() {
        return "this is a demo~";
    }
    
    @RequestMapping("/")
    public String index(Model model,HttpServletRequest request) {
    	HttpSession session = request.getSession();
    	model.addAttribute("sid", session.getId());
    	log.info("初始化websocket页面~");
		return "index";   	
    }
    
}
