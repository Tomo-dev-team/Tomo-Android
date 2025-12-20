package com.example.tomo.Friends;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FriendErrorCode {

    FRIEND_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 친구입니다"
    ),
    FRIEND_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "친구를 찾을 수 없습니다"
    ),
    UNAUTHORIZED_USER(
            HttpStatus.UNAUTHORIZED,
            "로그인된 사용자가 아닙니다"
    );

    private final HttpStatus status;
    private final String message;
}
