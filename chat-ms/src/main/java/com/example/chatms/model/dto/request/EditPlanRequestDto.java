package com.example.chatms.model.dto.request;

import com.example.chatms.model.entity.PlanItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EditPlanRequestDto {
    String firstUser;
    String secondUser;
    List<PlanItem> planItemList;
}




