package ru.gnivc.portalservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gnivc.portalservice.util.Role;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email; //username in keycloak
    @Column(name = "first-name")
    private String firstName;
    @Column(name = "last-name")
    private String lastName;
    private String role;
}
