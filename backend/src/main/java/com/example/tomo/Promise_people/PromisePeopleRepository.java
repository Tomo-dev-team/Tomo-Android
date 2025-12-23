package com.example.tomo.Promise_people;

import com.example.tomo.Promise.Promise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromisePeopleRepository extends JpaRepository<Promise_people, Long> {

    @Query("""
    select pp.promise
    from Promise_people pp
    where pp.user.id = :userId
    """)
    List<Promise> findPromisesByUserId(Long userId);



}
