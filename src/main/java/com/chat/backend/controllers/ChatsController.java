package com.chat.backend.controllers;

import com.chat.backend.services.ChatsService;
import com.chat.backend.services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatsController {

    private final ChatsService messageService;
    private SimpMessagingTemplate messagingTemplate;

    public ChatsController(
            ChatsService messageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(messageService.getAllUsersAndGroups(), HttpStatus.OK);
    }

    @GetMapping("/count/{token}")
    public void getNewMessageCount(@PathVariable String token) {
        List<Map<String,Object>> messages = messageService.loadMessageUpdates();
        System.out.println(messages);
        messagingTemplate.convertAndSend(
                "/topic/updates/" + token,
                messages
        );
    }

}
