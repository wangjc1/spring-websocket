
## 简介(基于XML方式配置)

在SpringMVC中有两种实现WebSocket实时通信的方式：

1. 另外一个是使用Socket.IO协议实现。
2. 一个是使用WebSocket的一个子协议stomp


本项目演示如何通过stomp协议实现

### WebSocket js客户端测试
在IDEA中直接右键运行stomp.html页面 => 点击“连接”按钮
```
/client/html/ws.html 测试点对点交互发送消息(有问题，连不上服务)
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

基于XML配置中问题：
```
 1. Shiro拦截的问题，解决办法就是配置白名单。
 2. SpringMVC缺省ServletHandler标签导致的问题
 3. web容器不支持异步请求
 4. 点对点推送消息测试有问题
 解决方案请请看：<简书>
```

## 许可证********
Copyright (c) 2018 alex.wang


