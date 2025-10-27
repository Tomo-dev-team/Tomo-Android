package com.example.tomo.global;

public class NotLeaderUserException extends RuntimeException {
    public NotLeaderUserException(String message) {
        super(message);
    }
}
