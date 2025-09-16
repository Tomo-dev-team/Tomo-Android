package com.example.tomo.Moim_people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoimPeopleRepository extends JpaRepository<Moim_people, Long> {

    @Query("SELECT m FROM Moim_people m WHERE m.user.id = :user_id")
    List<Moim_people> findByUserId(@Param("user_id") Long userId);

}
