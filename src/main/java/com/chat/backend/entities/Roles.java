package com.chat.backend.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Roles {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String role;
    @OneToMany(mappedBy = "role")
    private List<Roles> roles;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
