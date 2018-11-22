package com.wang.springmvc.controller;

import com.wang.springmvc.handler.SocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
public class HelloWorldController {

	@Autowired
	private SimpMessagingTemplate stompMessage;

	@Autowired
	@Qualifier("wsHandler")
	private SocketHandler wsHandler;

	//@PostConstruct
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
				stompMessage.convertAndSend("/topic/notice",m);
			}
		},10, 5, TimeUnit.SECONDS);
	}

	@ResponseBody
	@RequestMapping("/stomp/send")
	public String stompSend(){
		String m = "当前时间:"+new Date();
		stompMessage.convertAndSend("/topic/notice",m);
		return "ok";
	}

	@ResponseBody
	@RequestMapping("/ws/send")
	public String wsSend(){
		String m = "当前时间:"+new Date();
		wsHandler.sendMessagesToUsers(new TextMessage(m));
		return "ok";
	}
}
