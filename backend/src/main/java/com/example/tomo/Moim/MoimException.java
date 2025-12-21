package com.example.tomo.Moim;

import lombok.Getter;

@Getter
public class MoimException extends RuntimeException {

    private final MoimErrorCode errorCode;

    public MoimException(MoimErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

