package com.example.planms.model.entity;

import com.example.planms.model.enums.GoalType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "goal_date")
    LocalDate date;

    String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type")
    GoalType goalType;

    @Builder.Default
    boolean notePointStatus = false;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    List<GoalContainer> goalContainers;

    @OneToOne(mappedBy = "goal", cascade =  CascadeType.ALL, orphanRemoval = true)
    GoalItem goalItem;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Plan> plans;
}


