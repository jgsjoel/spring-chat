package com.chat.backend.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ActiveUserService {

    private final Map<String, Set<String>> userActiveChannels = new ConcurrentHashMap<>();

    public void addChannelToUser(String userToken,String channelToken) {
        userActiveChannels.computeIfAbsent(userToken, k -> ConcurrentHashMap.newKeySet()).add(channelToken);
    }

    public Set<String> findChannelsByToken(String userToken) {
        return userActiveChannels.get(userToken);
    }

    public Set<String> findUsersByChannel(String channelToken) {
        return userActiveChannels.entrySet().stream()
                .filter(entry -> entry.getValue().contains(channelToken))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void removeUserByToken(String userToken) {
        userActiveChannels.remove(userToken);
    }



}
