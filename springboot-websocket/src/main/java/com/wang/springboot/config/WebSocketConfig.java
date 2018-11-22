package com.wang.springboot.config;

import com.wang.springboot.handler.SocketHandler;
import com.wang.springboot.interceptor.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private SocketHandler socketHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(socketHandler, "/ws/point")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }

   /* //注意:如果去掉SocketHandler上面的@Component，直接在这里加载，会有问题的，因为Controller会先于这里加载
    @Bean
    public TextWebSocketHandler webSocketHandler(){
        return new SocketHandler();
    }*/
}
