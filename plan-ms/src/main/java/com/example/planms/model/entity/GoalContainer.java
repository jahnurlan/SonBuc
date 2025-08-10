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
public class GoalContainer {
    @Version
    int version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;
    int index;
    @Column(name = "goal_date")
    LocalDate date;
    String username;

    @ManyToOne
    @JsonIgnore
    Goal goal;

    @OneToMany(mappedBy = "goalContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    List<GoalPlanItem> planItemList;
}


