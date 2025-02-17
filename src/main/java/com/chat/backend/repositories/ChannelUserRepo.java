package com.chat.backend.repositories;

import com.chat.backend.entities.Channel;
import com.chat.backend.entities.ChannelUsers;
import com.chat.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChannelUserRepo extends JpaRepository<ChannelUsers,Long> {

    void deleteChannelUsersByChannel(Channel channel);
    void deleteChannelUsersByUser(User user);
    Optional<ChannelUsers> findChannelUsersByChannelAndUser(Channel channel, User user);
    List<ChannelUsers> findChannelUsersByChannel(Channel channel);

    @Query("select count(m), m.channel.channelToken,m.sender.token,m.channel.isGroup,m.channel.Name" +
            " from Message m" +
            " where m.channel in (select cu.channel from ChannelUsers cu where cu.user.email=:email)" +
            " and m.sentAt > (select u.last_seen from User u" +
            " where u.token = :email)" +
            " and m.sender.token != :email" +
            " group by m.channel")
    List<Object[]> findMessageNewMessageCountForUser(@Param("email")String email);

    @Query("select cu from ChannelUsers cu where cu.user.token=:token")
    List<ChannelUsers> findChannelsByUserToken(@Param("token")String token);


}
