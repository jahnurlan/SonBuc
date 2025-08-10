package com.example.chatms.model.dto.request;

import com.example.chatms.model.entity.PlanContainer;
import com.example.chatms.model.entity.PlanItem;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaRequest {
    String firstUser;
    String secondUser;
    List<PlanItem> planItemList;
    List<PlanContainer> planContainerList;
    LocalDate planDate;
}
