package com.example.planms.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanNoteUpdateRequest {
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}