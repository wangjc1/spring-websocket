
package com.wang.springmvc.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个Controller不是必需的，是用来解决<mvc:default-servlet-handler/>标签导致的问题，解决方案有两种：
 *  一种是把websocket配置添加到dispatch-servlet.xml配置文件中
 *  另一种就是把SockJsHttpRequestHandler改造成一个Controller，这样就不会被缺省的Servlet(Tomcat的servlet)给冲掉了
 */
@Controller
public class SockJsHttpRequestController implements InitializingBean {

	@Autowired
	private SockJsService sockJsService;

	@Autowired
	private WebSocketHandler webSocketHandler;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.webSocketHandler = new ExceptionWebSocketHandlerDecorator(new LoggingWebSocketHandlerDecorator(webSocketHandler));
	}

	@RequestMapping(value="/stat/websocket/**/{path}")
	public void handleRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse,@PathVariable String path)
			throws ServletException, IOException {
		//websocket默认是使用SimpleUrlHandlerMapping映射的，第一次是先请求info
		servletRequest.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, path);

		ServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
		ServerHttpResponse response = new ServletServerHttpResponse(servletResponse);
		try {
			this.sockJsService.handleRequest(request, response, getSockJsPath(servletRequest), this.webSocketHandler);
		}
		catch (Throwable ex) {
			throw new SockJsException("Uncaught failure in SockJS request, uri=" + request.getURI(), ex);
		}
	}

	private String getSockJsPath(HttpServletRequest servletRequest) {
		String attribute = HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;
		String path = (String) servletRequest.getAttribute(attribute);
		return (path.length() > 0 && path.charAt(0) != '/' ? "/" + path : path);
	}
}
