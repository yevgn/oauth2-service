package ru.antonov.oauth2_social.exception;

public class ClientNotFoundException extends RuntimeException{
    public ClientNotFoundException(String msg){
        super(msg);
    }
}
