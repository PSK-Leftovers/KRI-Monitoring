package com.leftovers.kri.indicator;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "indicator_thresholds")
@Getter
@Setter
public class IndicatorThresholds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private Long indicatorId;

    @Column(nullable = false)
    private double green;
    @Column(nullable = false)
    private double yellow;
    @Column(nullable = false)
    private double red;

    @Column(nullable = false)
    private Instant changedAt;

    @PrePersist
    public void setChangedAt() {
        if (changedAt == null)
            changedAt = Instant.now();
    }
}
