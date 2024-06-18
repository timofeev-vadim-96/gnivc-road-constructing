package ru.gnivc.portalservice.model;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "keycloak_companies")
public class KeycloakCompany {
    @Id
    @NotNull
    @Column(unique = true)
    private String id; //id in keycloak realm

    @NotNull
    @Column(unique = true)
    private String name;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyEntity companyId;
}
