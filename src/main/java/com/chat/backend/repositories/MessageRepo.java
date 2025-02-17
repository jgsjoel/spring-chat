package com.chat.backend.repositories;


import com.chat.backend.entities.Channel;
import com.chat.backend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.channel.channelToken=:token")
    List<Message> findMessagesByChannelToken(@Param("token") String token);

}
