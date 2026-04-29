package com.leftovers.kri.indicator;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "indicator")
@Getter
@Setter
public class Indicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double greenThreshold;
    private Double yellowThreshold;
    private Double redThreshold;
}