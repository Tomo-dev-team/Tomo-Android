package com.example.tomo.Moim;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoimController {

    private final MoimService moimService;

    public MoimController(MoimService moimService) {
        this.moimService = moimService;
    }

    @PostMapping("/moims")
    public Long moim(@RequestBody addMoimRequestDto dto) {
        return moimService.addMoim(dto);
    }


}
