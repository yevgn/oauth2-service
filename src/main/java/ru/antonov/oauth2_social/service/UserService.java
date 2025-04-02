package ru.antonov.oauth2_social.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.antonov.oauth2_social.entity.UserEntity;
import ru.antonov.oauth2_social.exception.UserNotFoundException;
import ru.antonov.oauth2_social.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean checkUserExistsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public UserEntity findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(String.format("Пользователь с %s email не найден", email))
        );
    }
}
