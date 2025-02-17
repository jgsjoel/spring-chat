package com.chat.backend.events;

import com.chat.backend.entities.ChannelUsers;
import com.chat.backend.repositories.ChannelUserRepo;
import com.chat.backend.repositories.MessageRepo;
import com.chat.backend.services.ActiveUserService;
import com.chat.backend.services.ChannelService;
import com.chat.backend.services.UserService;
import org.springframework.context.ApplicationEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SocketListener implements ApplicationListener<ApplicationEvent> {

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<String, String> subscriptions = new ConcurrentHashMap<>();

    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelUserRepo channelUserRepo;
    private final ActiveUserService activeUserService;
    private final UserService userService;

    public SocketListener(SimpMessagingTemplate messagingTemplate, ChannelUserRepo channelUserRepo, ActiveUserService activeUserService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.channelUserRepo = channelUserRepo;
        this.activeUserService = activeUserService;
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectEvent) {
            SessionConnectEvent connectEvent = (SessionConnectEvent) event;
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(connectEvent.getMessage());
            String token = accessor.getFirstNativeHeader("Authorization");

            System.out.println("connect session id: " + accessor.getSessionId());

            userSessions.put(accessor.getSessionId(), token);
            findAndAddUsersChannels(token);

        }

        if (event instanceof SessionDisconnectEvent) {
            SessionDisconnectEvent disconnectEvent = (SessionDisconnectEvent) event;
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());

            System.out.println("disconnect session id: " + accessor.getSessionId());

            // Get the token before removing the session
            String token = userSessions.get(accessor.getSessionId());
            userService.updateLastSeen(token);

            if (token != null) {
                removeUser(token);
                userSessions.remove(accessor.getSessionId());

                System.out.println("------disconnec start-------");
                System.out.println(activeUserService.findChannelsByToken(token));  // Use the token directly
                System.out.println("------disconnec end-------");
            } else {
                System.out.println("No token found for session id: " + accessor.getSessionId());
            }
        }

    }

    private void findAndAddUsersChannels(String userToken) {
        for (ChannelUsers cu : channelUserRepo.findChannelsByUserToken(userToken)) {
            activeUserService.addChannelToUser(userToken, cu.getChannel().getChannelToken());
        }
    }

    private void removeUser(String userToken) {
        activeUserService.removeUserByToken(userToken);
    }


}

