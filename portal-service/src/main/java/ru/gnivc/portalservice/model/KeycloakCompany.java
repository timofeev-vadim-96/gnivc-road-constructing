package ru.gnivc.portalservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "keycloak_companies")
public class KeycloakCompany {
    @Id
    @NotNull
    @Column(unique = true)
    private String id;
    @NotNull
    @Column(unique = true)
    private String name;
}
