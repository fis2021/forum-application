package org.fis2021.exceptions;

public class UserNotFoundException extends Exception{
    private String username;
    public UserNotFoundException(String username) {
        super(String.format("The username %s is not registered!", username));
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
