package ru.antonov.oauth2_social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.antonov.oauth2_social.entity.TokenEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    Optional<TokenEntity> findByToken(String token);

    boolean existsByToken(String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE TokenEntity SET revoked = true, expired = true WHERE user.email = :email")
    int revokeAllTokensByUserEmail(String email);
}
