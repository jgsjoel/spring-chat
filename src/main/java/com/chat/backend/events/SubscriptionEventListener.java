package com.chat.backend.events;

import com.chat.backend.services.ActiveUserService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SubscriptionEventListener implements ApplicationListener<ApplicationEvent> {

    private final Map<String, String> subscriptions = new ConcurrentHashMap<>();
    private final ActiveUserService activeUserService;

    public SubscriptionEventListener(ActiveUserService activeUserService) {
        this.activeUserService = activeUserService;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionSubscribeEvent subscribeEvent) {
            handleSubscribeEvent(subscribeEvent);
        }

        if (event instanceof SessionUnsubscribeEvent unsubscribeEvent) {
            handleUnsubscribeEvent(unsubscribeEvent);
        }
    }

    private void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        if (destination != null) {
            subscriptions.put(sessionId, destination);
            System.out.println("Subscribed to: " + destination);
        }
    }

    private void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = subscriptions.remove(sessionId);

        if (destination != null) {
            System.out.println("Unsubscribed from: " + destination);
        }
    }
}

