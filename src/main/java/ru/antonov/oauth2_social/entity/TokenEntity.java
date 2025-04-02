package ru.antonov.oauth2_social.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_mode")
    private TokenMode tokenMode;

    private boolean expired = false;

    private boolean revoked = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public static TokenEntity makeWithDefaults(
            String token,  TokenType tokenType, TokenMode tokenMode, UserEntity userEntity){
        return TokenEntity
                .builder()
                .token(token)
                .tokenType(tokenType)
                .tokenMode(tokenMode)
                .user(userEntity)
                .build();
    }
}

