package com.leftovers.kri.indicator.thresholds;

import java.time.Instant;

import com.leftovers.kri.indicator.Indicator;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "indicator_thresholds")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Thresholds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "indicator_id", nullable = false)
    private Indicator indicator;

    @Column(nullable = false)
    private double greenThreshold;
    @Column(nullable = false)
    private double yellowThreshold;
    @Column(nullable = false)
    private double redThreshold;

    @Column(nullable = false)
    private Instant recordedAt;

    @PrePersist
    public void setRecordedAt() {
        if (recordedAt == null) {
            recordedAt = Instant.now();
        }
    }
}
