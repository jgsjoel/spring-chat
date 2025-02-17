package com.chat.backend.controllers;

import com.chat.backend.dto.requests.MessageReq;
import com.chat.backend.entities.Message;
import com.chat.backend.events.SocketListener;
import com.chat.backend.services.ActiveUserService;
import com.chat.backend.services.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://localhost:3000/*")
public class WSMsgController {

    private final ActiveUserService activeUserService;
    private MessageService messageService;

    private SocketListener socketListener;

    private SimpMessagingTemplate messagingTemplate;

    public WSMsgController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate,
            SocketListener socketListener,
            ActiveUserService activeUserService) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.socketListener = socketListener;
        this.activeUserService = activeUserService;
    }

    @MessageMapping("/message/{channel}")
    public void mapMessage(@DestinationVariable String channel, @RequestBody MessageReq request) {

        System.out.println("------controller-------"+request.file_url());
        Message newMessage  = messageService.saveMessage(channel, request);

        Map<String,Object> response = new HashMap<>();
        response.put("sender",newMessage.getSender().getUserfName());
        response.put("sender_token",newMessage.getSender().getToken());
        response.put("message",newMessage.getMessage());
        response.put("localDateTime",newMessage.getSentAt());
        response.put("file_url",(newMessage.getAttachmentFileName().isEmpty())? "": newMessage.getAttachmentFileName());
        //regardless if there is one or two people this should stay
        messagingTemplate.convertAndSend(
                "/topic/channel/" + channel,
                response
        );

        //to all connected users who are members of the channel send the notification
        //avoid sender
        //this will skip the sender
        for (String userToken : activeUserService.findUsersByChannel(channel)) {
            if (!userToken.equals(request.sender_token())) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", request.sender());
                map.put("token", (!newMessage.getChannel().isGroup()) ?
                        request.sender_token():channel
                );
                messagingTemplate.convertAndSend(
                        "/topic/chats/" + userToken,
                        map
                );
            }
        }

    }


}
