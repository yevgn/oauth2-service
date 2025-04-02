package ru.antonov.oauth2_social.exception;

public class TokenConfigurationException extends RuntimeException{
    public TokenConfigurationException(String msg){
        super(msg);
    }
}
