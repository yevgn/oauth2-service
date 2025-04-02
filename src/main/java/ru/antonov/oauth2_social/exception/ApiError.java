package ru.antonov.oauth2_social.exception;


import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;
}