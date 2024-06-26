package ru.gnivc.logistservice.model;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name = "vehicles")
public class VehicleEntity {
    @Id
    private Long id;

    @Column(name = "state_number")
    private String stateNumber;
}
