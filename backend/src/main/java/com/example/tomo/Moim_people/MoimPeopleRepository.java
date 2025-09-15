package com.example.tomo.Moim_people;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoimPeopleRepository extends JpaRepository<Moim_people, Long> {

}
