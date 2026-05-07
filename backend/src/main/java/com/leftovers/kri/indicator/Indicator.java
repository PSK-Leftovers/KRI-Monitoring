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

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    private Double greenThreshold;
    private Double yellowThreshold;
    private Double redThreshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndicatorStatus status = IndicatorStatus.UNKNOWN;
}