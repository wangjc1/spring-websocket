package com.wang.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
public class HelloWorldController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

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
				messagingTemplate.convertAndSend("/topic/notice",m);
			}
		},10, 5, TimeUnit.SECONDS);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String sayHello(ModelMap model) {
		model.addAttribute("greeting", "Hello World from Spring 4 MVC");
		return "welcome";
	}


	@RequestMapping(value="/helloagain", method = RequestMethod.GET)
	public String sayHelloAgain(ModelMap model) {
		model.addAttribute("greeting", "Hello World Again, from Spring 4 MVC");
		return "welcome";
	}

}
