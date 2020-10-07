# 一、通俗解释链接

https://www.zhihu.com/question/20215561/answer/40316953

# 二、整合 WebSocket 基于 STOMP 协议实现广播
## 1、 **什么是 WebSocket ？**

WebSocket 为浏览器和服务器提供了双工异步通信的功能，即浏览器可以向服务器发送信息，反之也成立。

WebSocket 是通过一个 socket 来实现双工异步通信能力的，但直接使用 WebSocket ( 或者 SockJS：WebSocket 协议的模拟，增加了当前浏览器不支持使用 WebSocket 的兼容支持) 协议开发程序显得十分繁琐，所以使用它的子协议 STOMP。

## 2、**STOMP 协议简介**

它是高级的流文本定向消息协议，是一种为 MOM (Message Oriented Middleware，面向消息的中间件) 设计的简单文本协议。

它提供了一个可互操作的连接格式，允许 STOMP 客户端与任意 STOMP 消息代理 (Broker) 进行交互，类似于 OpenWire (一种二进制协议)。

由于其设计简单，很容易开发客户端，因此在多种语言和多种平台上得到广泛应用。其中最流行的 STOMP 消息代理是 Apache ActiveMQ。

STOMP 协议使用一个基于 (frame) 的格式来定义消息，与 Http 的 request 和 response 类似 。

##  3、**广播**

接下来，实现一个广播消息的 demo。即服务端有消息时，将消息发送给所有连接了当前 endpoint 的浏览器。

## 4、实例

### 1）、配置类

```
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
//@EnableWebSocketMessageBroker注解用于开启使用STOMP协议来传输基于代理（MessageBroker）的消息。这时候控制器（controller）
//开始支持@MessageMapping，就像是使用@requestMapping一样。
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册一个stomp的节点（endpoint），并指定使用SocketJS协议。
        registry.addEndpoint("/endpointNasus").withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //广播式配置名为/nasus消息代理，这个消息代理必须和controller中的 @SendTo 配置的地址前缀一样或者全匹配
        registry.enableSimpleBroker("/nasus");
    }
}
```

### 2）、两个pojo

```
package com.ph.stomp.pojo;

public class ClientMessage {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```
package com.ph.stomp.pojo;

public class ServerMessage {
    private String responseMessage;

    public ServerMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
```

### 3）、两个控制器

```

@RestController
public class WebSocketController {
    @MessageMapping("/hello") // @MessageMapping 和 @RequestMapping 功能类似，浏览器向服务器发起消息，映射到该地址。
    @SendTo("/nasus/getResponse") // 如果服务器接受到了消息，就会对订阅了 @SendTo 括号中的地址的浏览器发送消息。
    public ServerMessage say(ClientMessage message) throws Exception {
        Thread.sleep(3000);
        return new ServerMessage("Hello," + message.getName() + "!");
    }
}
```

```
@Controller
public class ViewController {
    @GetMapping("/nasus")
    public String getView(){
        return "nasus";
    }
}
```

### 4）、主页面

```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <title>Spring Boot+WebSocket+广播式</title>

</head>
<body onload="disconnect()">
<noscript><h2 style="color: #ff0000">貌似你的浏览器不支持websocket</h2></noscript>
<div>
    <div>
        <button id="connect" onclick="connect();">连接</button>
        <button id="disconnect" disabled="disabled" onclick="disconnect();">断开连接</button>
    </div>
    <div id="conversationDiv">
        <label>输入你的名字</label><input type="text" id="name" />
        <button id="sendName" onclick="sendName();">发送</button>
        <p id="response"></p>
    </div>
</div>
<script th:src="@{http://localhost:8080/webjars/sockjs-client/1.1.2/sockjs.js}"></script>
<!--<script th:src="@{http://localhost:8080/webjars/stomp-js/ca2e094679/stomp.js}"></script>-->
<script th:src="@{http://localhost:8080/webjars/jquery/3.3.1-2/jquery.js}"></script>
<script th:src="@{stomp.js}"></script>
<script type="text/javascript">
    var stompClient = null;

    function setConnected(connected) {
        document.getElementById('connect').disabled = connected;
        document.getElementById('disconnect').disabled = !connected;
        document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
        $('#response').html();
    }

    function connect() {
        // 连接 SockJs 的 endpoint 名称为 "/endpointNasus"
        var socket = new SockJS('/endpointNasus');
        // 使用 STOMP 子协议的 WebSocket 客户端
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            // 通过 stompClient.subscribe 订阅 /nasus/getResponse 目标发送的信息，对应控制器的 SendTo 定义
            stompClient.subscribe('/nasus/getResponse', function(respnose){
                // 展示返回的信息，只要订阅了 /nasus/getResponse 目标，都可以接收到服务端返回的信息
                showResponse(JSON.parse(respnose.body).responseMessage);
            });
        });
    }


    function disconnect() {
        // 断开连接
        if (stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function sendName() {
        // 向服务端发送消息
        var name = $('#name').val();
        // 通过 stompClient.send 向 /hello （服务端）发送信息，对应控制器 @MessageMapping 中的定义
        stompClient.send("/hello", {}, JSON.stringify({ 'name': name }));
    }

    function showResponse(message) {
        // 接收返回的消息
        var response = $("#response");
        response.html(message);
    }
</script>
</body>
</html>
```

