package com.example.tomo.Promise;

import lombok.Getter;

@Getter
public class PromiseException extends RuntimeException {

    private final PromiseErrorCode errorCode;

    public PromiseException(PromiseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}

