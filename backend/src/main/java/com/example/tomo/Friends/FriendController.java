package com.example.tomo.Friends;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendController {

    private final FriendService friendService;
    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/friends")
    public ResponseEntity<List<ResponseGetFriendsDto>> getMyFriends(){
        return ResponseEntity.ok(friendService.getFriends());
    }

    @GetMapping("/friends/detail")
    public ResponseEntity<List<ResponseFriendDetailDto>> getFriendDetails(){
        return ResponseEntity.ok().body(friendService.getDetailFriends());
    }

}
