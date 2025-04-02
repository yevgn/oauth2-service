package ru.antonov.oauth2_social.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.antonov.oauth2_social.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String email);
}
