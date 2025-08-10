package com.example.planms.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanItem {
    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int index;
    String text;
    String status;

    @Column(name = "username")
    String username;
    LocalDate planDate;

    @ManyToOne
    @JsonIgnore
    PlanContainer planContainer;

    @ManyToOne
    @JsonIgnore
    GoalPlanItem goalPlanItem;
}



