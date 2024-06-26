package ru.gnivc.dwhservice.model;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "companies_statistics")
public class CompanyStatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_trips")
    private int tripCreated;

    @Column(name = "started_trips")
    private int tripStarted;

    @Column(name = "ended_trips")
    private int tripEnded;

    @Column(name = "canceled_trips")
    private int tripCanceled;

    @Column(name = "accident_cases")
    private int tripAccident;

    private int tasks;

    @OneToOne
    @JoinColumn(name = "company")
    private CompanyEntity company;
}
