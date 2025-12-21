package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.global.Exception.DuplicatedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final MoimRepository moimRepository;

    //약속 생성
    @Transactional
    public ResponsePostUniformDto addPromise(addPromiseRequestDTO dto){

        Moim moim = moimRepository.findByTitle(dto.getTitle())
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND)
                );

        boolean duplicated =
                promiseRepository.existsByPromiseName(dto.getPromiseName()) &&
                        promiseRepository.existsByPromiseDateAndPromiseTime(
                                dto.getPromiseDate(),
                                dto.getPromiseTime()
                        );

        if (duplicated) {
            throw new PromiseException(PromiseErrorCode.PROMISE_ALREADY_EXISTS);
        }

        Promise promise = new Promise(
                dto.getPromiseName(),
                dto.getPlace(),
                dto.getPromiseTime(),
                dto.getPromiseDate()
        );

        promise.setMoimBasedPromise(moim);
        promiseRepository.save(promise);

        return new ResponsePostUniformDto(
                true,
                promise.getPromiseName() + " 약속이 생성되었습니다"
        );
    }

    // 약속 단일 조회
    @Transactional(readOnly = true)
    public ResponseGetPromiseDto getPromise(String promiseName){

        Promise promise = promiseRepository.findByPromiseName(promiseName)
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.PROMISE_NOT_FOUND)
                );

        return new ResponseGetPromiseDto(
                promise.getPromiseName(),
                promise.getPromiseDate(),
                promise.getPromiseTime(),
                promise.getLocation()
        );
    }

    //모임의 모든 약속 조회
    @Transactional(readOnly = true)
    public List<ResponseGetPromiseDto> getAllPromise(String title){

        Moim moim = moimRepository.findByTitle(title)
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND)
                );

        return promiseRepository.findByMoimId(moim.getId());
    }
}
