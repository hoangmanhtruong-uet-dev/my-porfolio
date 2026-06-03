package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_users")
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String role; // "male" or "female"

    @Column(nullable = false)
    private String password;
}
