package com.example.tomo.Moim.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class getMoimResponseDTO {

    private String title;
    private String description;
    private Integer peopleCount;
    private Boolean leader;
    private LocalDateTime createdAt;

    public getMoimResponseDTO(String title, String description,
                              Integer peopleCount, Boolean leader, LocalDateTime createdAt) {
        this.title = title;
        this.description = description;
        this.peopleCount = peopleCount;
        this.leader = leader;
        this.createdAt = createdAt;

    }

}
