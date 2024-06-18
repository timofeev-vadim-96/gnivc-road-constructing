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
@Table(name = "companies")
public class CompanyEntity {
    @Id
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    public CompanyEntity(String companyName) {
        this.companyName = companyName;
    }
}
