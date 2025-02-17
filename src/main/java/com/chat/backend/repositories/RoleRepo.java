package com.chat.backend.repositories;

import com.chat.backend.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Roles,Long> {

    Optional<Roles> findRolesByRole(String role);

}

