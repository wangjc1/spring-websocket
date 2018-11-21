package com.wang.springboot.controller;

import com.wang.springboot.commons.RedisClientUtils;
import com.wang.springboot.model.RequestMessage;
import com.wang.springboot.model.ResponseMessage;
import com.wang.springboot.message.SpringSubscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * WsController
 */
@Controller
public class WsController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedisClientUtils redisClientUtils;

    /**
     * 启动一个线程，定时向前端发送消息，模拟一个异步通知，前端订阅 /topic/notice
     */
    @PostConstruct
    public void sendSyncMessage(){
        Executors.newScheduledThreadPool(1,new ThreadFactory(){
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"websocket-thread");
            }
        }).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String m = "当前时间:"+new Date();
                System.out.println(String.format("Send message %s to notice.html",m));
                messagingTemplate.convertAndSend("/topic/notice",m);
            }
        },10, 5,TimeUnit.SECONDS);
    }

    /**
     * sub/pub模型：生产消息
     * 1. 通过访问 http://localhost:8092/publish?data=hello redis
     * 2. 通过命令 127.0.0.1:6379> publish SpringSubscribe.TOPIC "hello redis"
     *
     * SubListener监听消息到达
     * @param data
     * @return
     * @see SpringSubscribe.onMessage()
     */
    @RequestMapping("/publish")
    public String publish(String data) {
        redisTemplate.convertAndSend(SpringSubscribe.TOPIC, data);
        return "welcome";
    }

    @MessageMapping("/welcome")
    @SendTo("/topic/say")
    public ResponseMessage say(RequestMessage message) {
        System.out.println(message.getName());
        return new ResponseMessage("welcome," + message.getName() + " !");
    }

    /**
     * 定时推送消息
     */
    @Scheduled(fixedRate = 1000)
    public void callback() {
        // 发现消息
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        messagingTemplate.convertAndSend("/topic/callback", "定时推送消息时间: " + df.format(new Date()));
    }
}
