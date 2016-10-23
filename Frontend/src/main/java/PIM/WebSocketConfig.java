package PIM;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		config.setApplicationDestinationPrefixes("/app","/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/hello").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/request").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/gmailItems").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/usercheck").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/theme").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/mapsettings").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/datasources").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/deactivate").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/update").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/saveimage").setAllowedOrigins("*").withSockJS();
		registry.addEndpoint("/requestimage").setAllowedOrigins("*").withSockJS();
		
		
	}

}
