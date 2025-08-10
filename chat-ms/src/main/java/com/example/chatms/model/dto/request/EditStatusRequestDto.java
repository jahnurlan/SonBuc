package com.example.chatms.model.dto.request;

import com.example.chatms.model.entity.PlanContainer;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditStatusRequestDto {
    Long id;
    String firstUser;
    String secondUser;
    List<PlanContainer> planContainerList;
}





