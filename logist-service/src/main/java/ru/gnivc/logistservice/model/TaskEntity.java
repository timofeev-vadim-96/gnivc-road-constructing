package ru.gnivc.logistservice.model;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.geom.Point2D;

@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_point")
    private Point2D.Double startPoint;

    @Column(name = "finish_point")
    private Point2D.Double finishPoint;

    @Column(name = "cargo_description")
    private String cargoDescription;

    @ManyToOne
    @JoinColumn(name = "driver")
    private DriverEntity driver;

    @ManyToOne
    @JoinColumn(name = "vehicle")
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "company")
    private CompanyEntity company;
}

