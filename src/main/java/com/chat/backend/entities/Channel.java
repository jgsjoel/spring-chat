package com.chat.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long Id;
    String Name;
    String channelToken;
    boolean isGroup;
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    List<ChannelUsers> channelUsers;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    List<Message> messages;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.channelToken = "ch-"+UUID.randomUUID().toString();
    }


}
