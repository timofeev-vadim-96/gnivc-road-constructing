package ru.gnivc.portalservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "companies")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(length = 10) //длина ИНН Юр.лица
    private String inn;
    private String address;
    @Column(length = 9) //длина КПП
    private String kpp;
    @Column(length = 13) //длина ОГРН
    private String ogrn;
}
