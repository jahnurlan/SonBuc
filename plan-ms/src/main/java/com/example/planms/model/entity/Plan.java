package com.example.planms.model.entity;

import com.example.planms.model.enums.PlanType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Table(
        name = "plan",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "plan_date"})}
)
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "username")
    String username;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    PlanType type = PlanType.NORMAL;

    @Builder.Default
    boolean status = true;

    @Column(name = "plan_date")
    LocalDate date;

    @Column(length = 1000)
    String note;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PlanContainer> planContainerList;

    @ManyToOne
    @ToString.Exclude
    Goal goal;

    @ManyToMany
    @JoinTable(
            name = "plan_connections",
            joinColumns = @JoinColumn(name = "plan_id"),
            inverseJoinColumns = @JoinColumn(name = "connected_plan_id")
    )
    @Builder.Default
    @ToString.Exclude
    Set<Plan> connectedPlans = new HashSet<>();

    //Bağlantıları tüm gruba yayacak metod
    public void connectToGroup(Plan otherPlan) {
        if (this.connectedPlans.add(otherPlan)) { // Eğer zaten ekliyse, tekrar eklenmez
            otherPlan.getConnectedPlans().add(this);

            // Yeni planın tüm bağlantılarını bu plana ekleyelim
            for (Plan plan : otherPlan.getConnectedPlans()) {
                this.connectedPlans.add(plan);
                plan.getConnectedPlans().add(this);
            }

            // Mevcut planın tüm bağlantılarını yeni plana ekleyelim
            for (Plan plan : this.connectedPlans) {
                otherPlan.getConnectedPlans().add(plan);
            }
        }
    }

    public Set<Plan> getConnectedPlans() {
        if (connectedPlans == null) {
            connectedPlans = new HashSet<>();
        }

        return connectedPlans;
    }
}






