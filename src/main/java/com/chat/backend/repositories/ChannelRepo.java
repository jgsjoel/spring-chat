package com.chat.backend.repositories;

import com.chat.backend.entities.Channel;
import com.chat.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepo extends JpaRepository<Channel, Long> {

    @Query("select c from Channel c where c.isGroup=true")
    List<Channel> findAllByGroupTrue();
    Optional<Channel> findByChannelToken(String channelToken);

    @Query("select c.Name,c.channelToken" +
            " from Channel c left join ChannelUsers cu on cu.channel=c" +
            " where cu.user.email=:email and c.isGroup=true")
    List<Object[]> findAllGroupsByUser(@Param("email")String email);

    @Query("select c.channelToken from Channel c" +
            " join ChannelUsers cu1 on c=cu1.channel" +
            " join ChannelUsers cu2 on c=cu2.channel" +
            " where cu1.user.email=:current and cu2.user.token=:token and c.isGroup=false")
    Optional<String> findChannelTokenBetweenUsers(@Param("current") String current, @Param("token") String token);

}
