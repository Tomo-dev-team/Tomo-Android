package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.FriendCalculatedDto;
import com.example.tomo.Users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {



    // 본인(user) 또는 상대(friend)가 포함된 모든 친구 관계 삭제
    void deleteAllByUserOrFriend(User user, User friend);

    // 본인과 친구 관계 조회
    Optional<Friend> findByUserAndFriend(User user, User friend);

    boolean existsByUserAndFriend(User user, User friend);

    @Modifying
    @Query("DELETE FROM Friend f WHERE f.user.id = :userId OR f.friend.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);

    Optional<Friend> findByUserIdAndFriendId(Long userId, Long friendId);
}
