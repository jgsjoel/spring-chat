package com.chat.backend.services;

import com.chat.backend.dto.requests.AddToGroupRequest;
import com.chat.backend.entities.Channel;
import com.chat.backend.entities.ChannelUsers;
import com.chat.backend.entities.User;
import com.chat.backend.exceptions.EntityNotFoundException;
import com.chat.backend.repositories.ChannelRepo;
import com.chat.backend.repositories.ChannelUserRepo;
import com.chat.backend.repositories.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChannelService {

    private ChannelRepo channelRepo;
    private ChannelUserRepo channelUserRepo;
    private UserRepo userRepo;

    public ChannelService(
            ChannelRepo channelRepo,
            ChannelUserRepo channelUserRepo,
            UserRepo userRepo
            ) {
        this.channelRepo = channelRepo;
        this.channelUserRepo = channelUserRepo;
        this.userRepo = userRepo;
    }

    public List<Map<String,Object>> getGroupUsers(String channel_token) {

        List<Map<String,Object>> groupUsers = new ArrayList<>();
        for(ChannelUsers channelUsers :channelUserRepo.findChannelUsersByChannel(getChannelByToken(channel_token))){
            Map<String,Object> user = new HashMap<>();
            user.put("user_token", channelUsers.getUser().getToken());
            user.put("user_name",channelUsers.getUser().getUserfName());
            user.put("channel_token", channelUsers.getChannel().getChannelToken());
            groupUsers.add(user);
        }

        for(User users :userRepo.findUsersExcludingChannel(getChannelByToken(channel_token))){
            Map<String,Object> user = new HashMap<>();
            user.put("user_token", users.getToken());
            user.put("user_name",users.getUserfName());
            user.put("channel_token", null);
            groupUsers.add(user);
        }

        return groupUsers;

    }

    public Channel getChannelByToken(String token) {
        Optional<Channel> channel = channelRepo.findByChannelToken(token);
        if(!channel.isPresent()) {
            throw new EntityNotFoundException("Channel No Found");
        }
        return channel.get();
    }

    public User getUserByToken(String token) {
        Optional<User> user = userRepo.findByToken(token);
        if(!user.isPresent()) {
            throw new EntityNotFoundException("User No Found");
        }
        return user.get();
    }

    public List<Map<String,Object>> getAllChannels() {
        List<Map<String,Object>> list = new ArrayList<>();
        for(Channel channel :channelRepo.findAllByGroupTrue()){
            Map<String,Object> map = new HashMap<>();
            map.put("id", channel.getId());
            map.put("name", channel.getName());
            map.put("channel_token",channel.getChannelToken());
            map.put("created_at",channel.getCreatedAt().toString());
            list.add(map);
        }
        return list;
    }

    public Map<String,Object> createNewChannel(String channelName){

        Channel channel = new Channel();
        channel.setName(channelName);
        channel.setGroup(true);
        channelRepo.save(channel);
        Channel newChannel = channelRepo.save(channel);

        Map<String,Object> map = new HashMap<>();
        map.put("id", newChannel.getId());
        map.put("name", newChannel.getName());
        map.put("channel_token",newChannel.getChannelToken());
        map.put("created_at",newChannel.getCreatedAt().toString());

        return map;
    }

    @Transactional
    public Object addOrRemoveUserFromGroup(AddToGroupRequest request){
        Optional<ChannelUsers> channel = channelUserRepo.findChannelUsersByChannelAndUser(
                getChannelByToken(request.channel_token()),
                getUserByToken(request.user_token())
        );

        if(!channel.isPresent()) {
            return addUserToGroup(request);
        }else{
            deleteCurrentUser(channel.get().getUser());
        }
        return null;
    }

    public Map<String,Object> addUserToGroup(AddToGroupRequest request) {
        ChannelUsers channelUsers = new ChannelUsers();
        channelUsers.setUser(getUserByToken(request.user_token()));
        channelUsers.setChannel(getChannelByToken(request.channel_token()));
        ChannelUsers channelises = channelUserRepo.save(channelUsers);

        Map<String,Object> map = new HashMap<>();
        map.put("id", channelises.getId());
        map.put("user_token",channelises.getUser().getToken());

        return map;
    }

    @Transactional
    public void deleteCurrentUser(User user){
        channelUserRepo.deleteChannelUsersByUser(user);
    }

    @Transactional
    public void deleteChannel(String channelToken) {
        channelUserRepo.deleteChannelUsersByChannel(getChannelByToken(channelToken));
        channelRepo.delete(getChannelByToken(channelToken));
    }

}
