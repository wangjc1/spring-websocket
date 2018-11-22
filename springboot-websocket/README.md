
## 简介(基于注解方式配置)

在SpringBoot中有两种实现WebSocket实时通信的方式：

1. 另外一个是使用Socket.IO协议实现。
2. 一个是使用WebSocket的一个子协议stomp


本项目演示如何通过stomp协议实现

### WebSocket js客户端测试
在IDEA中直接右键运行stomp.html页面 => 点击“连接”按钮
```
/client/html/ws.html 测试点对点交互发送消息
/client/html/stomp.html 测试类似消息服务的广播发送消息
```

### 通过请求URL发送消息
```
http://localhost:8080/ws/send 发送普通消息
http://localhost:8080/stomp/send 发送广播消息
```

后台定时任务不断发送广播消息
```
scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String m = "当前时间:"+new Date();
                System.out.println(String.format("Send message %s to notice.html",m));
                messagingTemplate.convertAndSend("/topic/notice",m);
            }
},10, 5,TimeUnit.SECONDS);
```

通过Redis给websocket生产消息，然后再推送给前端
```
  通过Redis生产消息：
  1. 通过访问 http://localhost:8080/publish?data=hello redis
  2. 通过命令 127.0.0.1:6379> publish SpringSubscribe.TOPIC xxx
```

遗留问题
```
1. 去掉SocketHandler上面的@Component注解，直接在WebSocketConfig中通过@Bean注解加载，
   结果发现在WsController中引用不到，因为容器启动时是先加载Controller的，而这时WebSocketConfig还没加载
   具体原因还不知道。
   参考： https://blog.csdn.net/Sadlay/article/details/82952163?utm_source=blogxgwz8

```

## 许可证********
Copyright (c) 2018 alex.wang


