package ru.antonov.oauth2_social.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.antonov.oauth2_social.dto.TokenRequestDto;
import ru.antonov.oauth2_social.service.TokenService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
@Slf4j
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@Valid @RequestBody TokenRequestDto tokenRequestDto){
        log.info("validateToken() in TokenController...");
        return ResponseEntity.ok(
                tokenService.isTokenValid(tokenRequestDto)
        );
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody TokenRequestDto tokenRequestDto){
        log.info("refreshAccessToken() in TokenController...");
        return ResponseEntity.ok(
                tokenService.refreshAccessToken(tokenRequestDto)
        );
    }
}
