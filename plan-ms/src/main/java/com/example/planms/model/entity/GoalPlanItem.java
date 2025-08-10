package com.example.planms.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoalPlanItem {
    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int index;
    String text;
    String status;

    @Column(name = "username", nullable = false)
    String username;
    @Column(name = "goal_date")
    LocalDate date;

    @ManyToOne
    @JsonIgnore
    GoalContainer goalContainer;

    @OneToMany(mappedBy = "goalPlanItem", cascade = CascadeType.ALL)
    List<PlanItem> planItemList;
}
