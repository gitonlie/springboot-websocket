<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String path = request.getContextPath();
    String basepath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>扫码页面</title>
</head>
<script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
	var websocket = null;
	socket();//初始化
	function socket(){
		//判断当前浏览器是否支持WebSocket  
		if ('WebSocket' in window) {  
		    websocket = new WebSocket("ws://localhost:8080/websocket/${sid}");  
		}  
		else {  
		    alert('Not support websocket');
		}
		
		//连接发生错误的回调方法
		websocket.onerror = function(){
			setMessage("连接发生错误");
		};
		
		//连接成功建立的回调方法
		websocket.onopen = function(event){
			console.log("连接成功建立");
		}
		
		//接收到消息的回调方法
		websocket.onmessage = function(event){
			setMessage(event.data);
		}
		
		//连接关闭的回调方法
		websocket.onclose = function(code,reason){
			setMessage("连接关闭");
		}
		
		//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
		window.onbeforeunload = function(){
		    websocket.close();
		}
	}
	
	//将消息显示在网页上
	function setMessage(message){
	    $('#message').append(message+"\n");
	}
	
	function sendMessage(){
	   var msg = $('#content').val();
	   websocket.send(msg);
	}
	
</script>
<body>
	<div>
		<table>
			<tr>
				<td><input type="button" value="发送消息" onclick="sendMessage()"/></td>
				<td>websocket服务端返回信息:</td>
			</tr>
			<tr>
				<td>
					<textarea rows="50" cols="100" id="content"></textarea>
				</td>
				<td>
					<textarea rows="50" cols="100" readonly="readonly" id="message"></textarea>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>