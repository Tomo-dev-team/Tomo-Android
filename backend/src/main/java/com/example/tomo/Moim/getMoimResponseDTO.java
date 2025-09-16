package com.example.tomo.Moim;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class getMoimResponseDTO {

    private String moimName;
    private String description;
    private Integer peopleCount;

    public getMoimResponseDTO(String moimName, String description, Integer peopleCount) {
        this.moimName = moimName;
        this.description = description;
        this.peopleCount = peopleCount;
    }

}
