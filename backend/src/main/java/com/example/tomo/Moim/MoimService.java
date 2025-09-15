package com.example.tomo.Moim;

import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoimService {

    private MoimRepository moimRepository;
    private UserRepository userRepository;
    private MoimPeopleRepository moimPeopleRepository;

    @Autowired
    public MoimService(MoimRepository moimRepository, UserRepository userRepository, MoimPeopleRepository moimPeopleRepository) {
        this.moimRepository = moimRepository;
        this.moimPeopleRepository = moimPeopleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long addMoim(addMoimRequestDto dto) {
        if (moimRepository.existsByMoimName(dto.getMoimName())) {
            throw new IllegalArgumentException("이미 존재하는 모임 이름입니다.");
        }

        Moim moim = new Moim(dto.getMoimName(), dto.getDescription());
        System.out.println("moim.getMoimName()= " + moim.getMoimName() + " moim.getDescription ="
        + moim.getDescription());
        moimRepository.save(moim);

        List<User> users = userRepository.findAllById(dto.getUserIds());
        for (User user : users) {
            Moim_people moim_people = new Moim_people(moim, user);
            moimPeopleRepository.save(moim_people);
        }

        return moim.getId();
    }
}

