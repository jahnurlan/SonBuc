package com.example.planms.model.dto.response;

import com.example.planms.model.entity.Plan;
import com.example.planms.model.entity.PlanContainer;
import com.example.planms.model.entity.PlanItem;
import com.example.planms.model.enums.PlanType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanDTO {
    Long id;
    String username;
    PlanType type;
    boolean status = true;
    List<PlanContainer> planContainerList;
    Long goalId;

    public static PlanDTO fromEntity(Plan plan) {
        if (plan == null) {
            return null;
        }

        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setUsername(plan.getUsername());
        dto.setType(plan.getType());
        dto.setStatus(plan.isStatus());
        dto.setPlanContainerList(plan.getPlanContainerList());
        dto.setGoalId(plan.getGoal() != null ? plan.getGoal().getId() : null);

        return dto;
    }

    public static PlanDTO fromEntityToShort(Plan plan) {
        if (plan == null) {
            return null;
        }

        PlanDTO dto = new PlanDTO();
        dto.setUsername(plan.getUsername());
        dto.setStatus(plan.isStatus());
        dto.setPlanContainerList(plan.getPlanContainerList());
        dto.setGoalId(plan.getGoal() != null ? plan.getGoal().getId() : null);

        return dto;
    }
}


