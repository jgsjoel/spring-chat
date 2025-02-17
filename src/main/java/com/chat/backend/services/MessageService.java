package com.chat.backend.services;

import com.chat.backend.dto.requests.MessageReq;
import com.chat.backend.entities.Channel;
import com.chat.backend.entities.ChannelUsers;
import com.chat.backend.entities.Message;
import com.chat.backend.repositories.ChannelRepo;
import com.chat.backend.repositories.ChannelUserRepo;
import com.chat.backend.repositories.MessageRepo;
import com.chat.backend.util.AuthUserUtil;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    private final ChannelRepo channelRepo;
    private final AuthUserUtil authUserUtil;
    private final ChannelUserRepo channelUserRepo;
    private final ActiveUserService activeUserService;
    private MessageRepo messageRepo;
    private UserService userService;
    private ChannelService channelService;
    public MessageService(
            MessageRepo messageRepo,
            UserService userService,
            ChannelService channelService,
            ChannelRepo channelRepo, AuthUserUtil authUserUtil, ChannelUserRepo channelUserRepo, ActiveUserService activeUserService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.channelService = channelService;
        this.channelRepo = channelRepo;
        this.authUserUtil = authUserUtil;
        this.channelUserRepo = channelUserRepo;
        this.activeUserService = activeUserService;
    }

    public Message saveMessage(String channelToken, MessageReq messageReq) {
        Message message = new Message();
        message.setChannel(channelService.getChannelByToken(channelToken));
        message.setSender(userService.getUserByToken(messageReq.sender_token()));
        message.setMessage(messageReq.message());
        System.out.println("----------------------"+messageReq.file_url()+"-------------------");
        message.setAttachmentFileName(messageReq.file_url());
        message.setRead(true);
        return messageRepo.save(message);
    }

    public Map<String, Object> loadMessagesWithUser(String token) {
        Map<String, Object> response = new HashMap<>();
        String channelToken;

        if (isGroup(token)) {
            channelToken = token;
            response.put("message_list", getGroupMessages(token));
        } else {
            Optional<String> tokenOpt = channelRepo.findChannelTokenBetweenUsers(authUserUtil.getAuthenticatedUser(), token);
            if (tokenOpt.isPresent()) {
                channelToken = tokenOpt.get();
                response.put("message_list", getMessagesByChannelToken(channelToken));
            } else {
                channelToken = createChannelWithUser(token);
                response.put("message_list", new ArrayList<>());
            }
        }

        response.put("channel_token", channelToken);
        return response;
    }

    private List<Map<String, Object>> getGroupMessages(String token) {
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Message message : messageRepo.findMessagesByChannelToken(token)) {
            messages.add(createMessageMap(message));
        }
        return messages;
    }

    private List<Map<String, Object>> getMessagesByChannelToken(String token) {
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Message message : messageRepo.findMessagesByChannelToken(token)) {
            messages.add(createMessageMap(message));
        }
        return messages;
    }


    private Map<String, Object> createMessageMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("sender", message.getSender().getUserfName());
        map.put("sender_token", message.getSender().getToken());
        map.put("message", message.getMessage());
        map.put("file_url",(message.getAttachmentFileName().isEmpty())?"":message.getAttachmentFileName());
        map.put("localDateTime", message.getSentAt());
        return map;
    }


    private String createChannelWithUser(String token) {
        Channel channel = new Channel();
        channel.setName("user-user");
        channel.setGroup(false);
        Channel newChannel = channelRepo.save(channel);

        // adding current user
        ChannelUsers currentUser = new ChannelUsers();
        currentUser.setChannel(newChannel);
        currentUser.setUser(userService.getUserByEmail(authUserUtil.getAuthenticatedUser()));
        ChannelUsers current = channelUserRepo.save(currentUser);

        // adding target user
        ChannelUsers otherUser = new ChannelUsers();
        otherUser.setChannel(newChannel);
        otherUser.setUser(userService.getUserByToken(token));
        ChannelUsers target = channelUserRepo.save(otherUser);

        //register channels to current user and target user in the active user service
        activeUserService.addChannelToUser(current.getUser().getToken(),current.getChannel().getChannelToken());

        //if the target user is not empty then add the new channel.
        if(!activeUserService.findChannelsByToken(target.getUser().getToken()).isEmpty()){
            activeUserService.addChannelToUser(target.getUser().getToken(),target.getChannel().getChannelToken());
        }

        return newChannel.getChannelToken();
    }

    private boolean isGroup(String token) {
        String firstPart = token.split("-")[0];
        if (firstPart.startsWith("us")) {
            return false;
        } else if (firstPart.startsWith("ch")) {
            return true;
        } else {
            throw new IllegalArgumentException("Invalid Token Provided");
        }
    }



}
