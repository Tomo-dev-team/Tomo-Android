package com.example.tomo.global.AOP;

import com.example.tomo.Friends.FriendErrorCode;
import com.example.tomo.Friends.FriendException;
import com.example.tomo.Moim.MoimErrorCode;
import com.example.tomo.Moim.MoimException;
import com.example.tomo.Promise.PromiseErrorCode;
import com.example.tomo.Promise.PromiseException;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.global.Exception.SelfFriendRequestException;
import com.example.tomo.global.ReponseType.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ApiResponse<?>> handleFriendException(FriendException e) {
        FriendErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(
                        code.name(),
                        code.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("INVALID_ARGUMENT", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("ENTITY_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"));
    }

    @ExceptionHandler(MoimException.class)
    public ResponseEntity<ApiResponse<?>> handleMoimException(MoimException e) {
        MoimErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(
                        code.name(),
                        code.getMessage()
                ));
    }
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(UserException e) {

        UserErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(code.name(), code.getMessage()));
    }
    @ExceptionHandler(SelfFriendRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleSelfFriend(SelfFriendRequestException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.failure("SELF_FRIEND_REQUEST", e.getMessage()));
    }

    @ExceptionHandler(PromiseException.class)
    public ResponseEntity<ApiResponse<Void>> handlePromiseException(PromiseException e) {

        PromiseErrorCode code = e.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.failure(code.name(), code.getMessage()));
    }



}

