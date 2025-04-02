package ru.antonov.oauth2_social.exception;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String msg){
        super(msg);
    }
}
