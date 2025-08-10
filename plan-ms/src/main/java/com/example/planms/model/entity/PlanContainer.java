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
public class PlanContainer {
    @Version
    int version;
    String username;
    LocalDate planDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;
    int index;
    Long timeNoteId;

    @ManyToOne
    @JsonIgnore
    Plan plan;

    @OneToMany(mappedBy = "planContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PlanItem> planItemList;
}


