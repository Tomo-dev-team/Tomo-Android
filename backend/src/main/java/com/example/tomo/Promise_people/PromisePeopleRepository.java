package com.example.tomo.Promise_people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromisePeopleRepository extends JpaRepository<Promise_people, Long> {

}
