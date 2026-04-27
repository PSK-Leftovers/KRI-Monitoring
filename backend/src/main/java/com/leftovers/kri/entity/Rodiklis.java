package com.leftovers.kri.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rodiklis")
@Getter
@Setter
public class Rodiklis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String pavadinimas;

    private String aprasymas;

    private Double ribaZalia;

    private Double ribaGeltona;

    private Double ribaRaudona;
}