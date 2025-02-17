package com.chat.backend;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Test {

    private static  final Map<String, Set<String>> userActiveChannels = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        userActiveChannels.computeIfAbsent("user123", k -> ConcurrentHashMap.newKeySet()).add("channelA");
        userActiveChannels.computeIfAbsent("user123", k -> ConcurrentHashMap.newKeySet()).add("channelB");
        userActiveChannels.computeIfAbsent("user123", k -> ConcurrentHashMap.newKeySet()).add("channelB");
        userActiveChannels.computeIfAbsent("user456", k -> ConcurrentHashMap.newKeySet()).add("channelC");
        userActiveChannels.computeIfAbsent("user476", k -> ConcurrentHashMap.newKeySet()).add("channelA");

        System.out.println(findUsersByChannel("channelA"));

    }

    public static void addNewUser(String userToken,String channelToken) {
        userActiveChannels.computeIfAbsent(userToken, k -> ConcurrentHashMap.newKeySet()).add(channelToken);
    }

    public static Set<String> findUsersByChannel(String channelToken) {
        return userActiveChannels.entrySet().stream()
                .filter(entry -> entry.getValue().contains(channelToken))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
