package com.matheus.ecommerce.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String token;

    private Instant expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String token, Instant expiresAt, User user){
        this.token = token;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public void setRefreshToken(String token, Instant expiresAt){
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
