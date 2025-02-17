package com.chat.backend.services;

import com.chat.backend.entities.User;
import com.chat.backend.repositories.ChannelRepo;
import com.chat.backend.repositories.ChannelUserRepo;
import com.chat.backend.repositories.MessageRepo;
import com.chat.backend.repositories.UserRepo;
import com.chat.backend.util.AuthUserUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatsService {

    private final UserRepo userRepo;
    private final ChannelRepo channelRepo;
    private final ChannelUserRepo channelUserRepo;
    private MessageRepo messageRepo;
    private AuthUserUtil authUserUtil;
    private UserService userService;

    public ChatsService(MessageRepo messageRepo, AuthUserUtil authUserUtil, UserService userService, UserRepo userRepo, ChannelRepo channelRepo, ChannelRepo channelRepo1, ChannelUserRepo channelUserRepo) {
        this.messageRepo = messageRepo;
        this.authUserUtil = authUserUtil;
        this.userService = userService;
        this.userRepo = userRepo;
        this.channelRepo = channelRepo1;
        this.channelUserRepo = channelUserRepo;
    }

    public List<Map<String,Object>> getAllUsersAndGroups(){
        List<Map<String,Object>> list = new ArrayList<>();
        for(User user: userRepo.findAllUsersExceptCurrent(authUserUtil.getAuthenticatedUser())){
            Map<String,Object> map = new HashMap<>();
            map.put("name", user.getUserfName());
            map.put("token", user.getToken());
            list.add(map);
        }

        for(Object[] obj:channelRepo.findAllGroupsByUser(authUserUtil.getAuthenticatedUser())){
            Map<String,Object> map = new HashMap<>();
            map.put("name", obj[0]);
            map.put("token", obj[1]);
            list.add(map);
        }

        return list;
    }

    //need to work on this later
    public List<Map<String,Object>> loadMessageUpdates() {
        List<Map<String,Object>> messages = new ArrayList<>();
        for(Object[] obj:channelUserRepo.findMessageNewMessageCountForUser(authUserUtil.getAuthenticatedUser())){
            Map<String,Object> map = new HashMap<>();
            map.put("message_count", obj[0]);
            map.put("token", ((boolean)obj[3])? obj[1]: obj[2]);
            map.put("name", obj[4]);
            messages.add(map);
        }
        return messages;
    }


}
