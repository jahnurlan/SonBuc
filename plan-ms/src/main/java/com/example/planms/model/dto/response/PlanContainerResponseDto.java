package com.example.planms.model.dto.response;

import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanContainerResponseDto {
    Long id;

    String name;
    int index;
    List<PlanItem> planItemList;
}


