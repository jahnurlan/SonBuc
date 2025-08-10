package com.example.chatms.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int index;
    String type;
    String text;
    String status;
    String username;

    @ManyToOne
    @JsonIgnore
    PlanContainer planContainer;
}



