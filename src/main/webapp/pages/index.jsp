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
<link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
<script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.3.1/jquery.min.js"></script>
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script type="text/javascript">
	var websocket = null;
	socket();//初始化
	function socket(){
		//判断当前浏览器是否支持WebSocket  
		if ('WebSocket' in window) {  
		    websocket = new WebSocket("ws://b8a53acb.ap.ngrok.io/websocket");  
		}  
		else {  
		    alert('Not support websocket');
		}
		
		//连接发生错误的回调方法
		websocket.onerror = function(){
		    setMessageInnerHTML("连接发生错误");
		};
		
		//连接成功建立的回调方法
		websocket.onopen = function(event){
		    setMessageInnerHTML("连接成功建立");
		}
		
		//接收到消息的回调方法
		websocket.onmessage = function(event){
			var message = event.data;
			console.log(message);
			if(message.indexOf("sessionId")!=-1){
				var s = message.split(":");
				var timestamp = new Date().getTime();				
				$('#qrcode').attr("src","/qrcode?sessionId="+s[1]+"&"+timestamp);
			}else{
				if(message=="二维码已刷新"){
					if(websocket!=null){
						websocket.close();
					}
					socket();//初始化
				}
				setMessageInnerHTML(message);
			}
		    
		}
		
		//连接关闭的回调方法
		websocket.onclose = function(){
		    setMessageInnerHTML("连接关闭");
		}
		
		//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
		window.onbeforeunload = function(){
		    websocket.close();
		}
	}
	
	//将消息显示在网页上
	function setMessageInnerHTML(innerHTML){
	    $('#message').append("<p>"+innerHTML+"</p>");
	}
	
	//关闭连接
	function closeWebSocket(){
	    websocket.close();
	}
	

</script>
<body>
<div id="message"></div>
<!-- Button trigger modal -->
<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal" style="margin-left: 10px">
  打开二维码
</button>

<!-- Modal -->
<div class="modal fade bs-example-modal-sm" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <!-- <h4 class="modal-title" id="myModalLabel">模拟登录</h4> -->
      </div>
      <div class="modal-body" align="center">
        	<img alt="" src="" id="qrcode"/>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>