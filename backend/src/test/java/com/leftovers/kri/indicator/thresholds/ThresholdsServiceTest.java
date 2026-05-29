package com.leftovers.kri.indicator.thresholds;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.leftovers.kri.indicator.thresholds.dto.ThresholdChange;
import com.leftovers.kri.indicator.thresholds.dto.ThresholdsResponse;

import ch.qos.logback.classic.filter.ThresholdFilter;

@ExtendWith(MockitoExtension.class)
public class ThresholdsServiceTest{
    
    @Mock
    ThresholdsRepository thresholdsRepository;
    @Mock
    ThresholdsSpecifications thresholdsSpecifications;
    @InjectMocks
    ThresholdsService thresholdsService;

    @Test
    void getThresholdChangesByIndicatorId_missingIndicator_throwsEntityNotFoundException() {

    }

    @Test
    void filterHistory_noAfterDateAndNoBeforeSpecfied_returnsUnfilteredList() {
        List<Thresholds> history = new ArrayList<>();
        history.add(Thresholds.builder().id(1L).greenThreshold(1).yellowThreshold(2).redThreshold(3).recordedAt(Instant.parse("2020-01-01T12:00:00Z")).build());
        history.add(Thresholds.builder().id(2L).greenThreshold(0.75).yellowThreshold(1.5).redThreshold(2).recordedAt(Instant.parse("2021-01-01T12:00:00Z")).build());
        history.add(Thresholds.builder().id(3L).greenThreshold(1).yellowThreshold(1.75).redThreshold(2.5).recordedAt(Instant.parse("2022-01-01T12:00:00Z")).build());

        //when(thresholdsRepository.findAll(ThresholdsSpecifications.))

        List<ThresholdChange> green = new ArrayList<>();
        green.add(ThresholdChange.builder().recordedAt(Instant.parse("2020-01-01T12:00:00Z")).value(1.0).build());
        green.add(ThresholdChange.builder().recordedAt(Instant.parse("2021-01-01T12:00:00Z")).value(0.75).build());
        green.add(ThresholdChange.builder().recordedAt(Instant.parse("2022-01-01T12:00:00Z")).value(1.0).build());
        
        List<ThresholdChange> yellow = new ArrayList<>();
        yellow.add(ThresholdChange.builder().recordedAt(Instant.parse("2020-01-01T12:00:00Z")).value(2.0).build());
        yellow.add(ThresholdChange.builder().recordedAt(Instant.parse("2021-01-01T12:00:00Z")).value(1.5).build());
        yellow.add(ThresholdChange.builder().recordedAt(Instant.parse("2022-01-01T12:00:00Z")).value(1.75).build());
        
        List<ThresholdChange> red = new ArrayList<>();
        red.add(ThresholdChange.builder().recordedAt(Instant.parse("2020-01-01T12:00:00Z")).value(3.0).build());
        red.add(ThresholdChange.builder().recordedAt(Instant.parse("2021-01-01T12:00:00Z")).value(2.0).build());
        red.add(ThresholdChange.builder().recordedAt(Instant.parse("2022-01-01T12:00:00Z")).value(2.5).build());

        ThresholdsResponse expectedResponse = ThresholdsResponse.builder().green(green).yellow(yellow).red(red).build();
    }

    @Test
    void getThresholdChangesByIndicatorId_noAfterDateSpecified_returnsUnfilteredByAfterDateList() {

    }

    @Test
    void getThresholdChangesByIndicatorId_noBeforeDateSpecified_returnsUnfilteredByBeforeDateList() {
        List<Thresholds> history = new ArrayList<>();
        //history.add(Thresholds.builder())

        //when(thresholdsRepository.findAllByIndicatorIdOrderByRecordedAtDesc(1L)).thenReturn();
    }

}
