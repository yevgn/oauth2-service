package ru.antonov.oauth2_social.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenRequestDto {
    @NotBlank(message = "Поле token отсутствует или является пустым")
    private String token;
}
