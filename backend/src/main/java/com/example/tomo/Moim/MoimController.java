package com.example.tomo.Moim;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/moims/{id}")
    public getMoimResponseDTO moimGet(@PathVariable Long id) {

        return moimService.getMoim(id);
    }
    @GetMapping("moims")
    public List<getMoimResponseDTO> getAllMoims(@RequestParam Long user_id) {
        return moimService.getMoimList(user_id);
    }



}
