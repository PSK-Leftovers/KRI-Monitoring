package com.leftovers.kri.indicator;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "indicator_entries")
@Getter
@Setter
public class IndicatorEntry {
    @Id
    private Long indicatorId;

    private Double entry;

    @Id
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime timestamp;
}
