package com.example.chatms.model.entity;

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
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    String name;
    int index;

    @ManyToOne
    @JsonIgnore
    ChatMessage chatMessage;

    @OneToMany(mappedBy = "planContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PlanItem> planItemList;
}


