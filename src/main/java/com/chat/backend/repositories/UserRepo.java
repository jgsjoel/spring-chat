package com.chat.backend.repositories;


import com.chat.backend.entities.Channel;
import com.chat.backend.entities.User;
import jakarta.persistence.NamedQueries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    public Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
    @Query("select u from User u where u.email != :email")
    List<User> findAllExCurrent(@Param("email") String email);

    @Query("select u from User u left join ChannelUsers cu on cu.user=u and cu.channel= :channel " +
            " where cu.user is null")
    List<User> findUsersExcludingChannel(@Param("channel") Channel channel);

    @Query("select u from User u where u.email!=:email")
    List<User> findAllUsersExceptCurrent(@Param("email") String email);

}
