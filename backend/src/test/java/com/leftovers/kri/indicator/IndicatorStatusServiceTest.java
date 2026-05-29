package com.leftovers.kri.indicator;

import com.leftovers.kri.indicator.thresholds.Thresholds;
import com.leftovers.kri.indicator.thresholds.ThresholdsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndicatorStatusServiceTest {

    @Mock ThresholdsRepository thresholdsRepository;
    @InjectMocks IndicatorStatusService indicatorStatusService;

    @Test
    void higherIsBetter_valueAboveYellow_returnsGreen() {
        stubThresholds(1L, 100.0, 50.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 150.0))
                .isEqualTo(IndicatorStatus.GREEN);
    }

    @Test
    void higherIsBetter_valueEqualToYellow_returnsGreen() {
        stubThresholds(1L, 100.0, 50.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 100.0))
                .isEqualTo(IndicatorStatus.GREEN);
    }

    @Test
    void higherIsBetter_valueBetweenRedAndYellow_returnsYellow() {
        stubThresholds(1L, 100.0, 50.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 75.0))
                .isEqualTo(IndicatorStatus.YELLOW);
    }

    @Test
    void higherIsBetter_valueEqualToRed_returnsYellow() {
        stubThresholds(1L, 100.0, 50.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 50.0))
                .isEqualTo(IndicatorStatus.YELLOW);
    }

    @Test
    void higherIsBetter_valueBelowRed_returnsRed() {
        stubThresholds(1L, 100.0, 50.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 25.0))
                .isEqualTo(IndicatorStatus.RED);
    }

    @Test
    void lowerIsBetter_valueBelowYellow_returnsGreen() {
        stubThresholds(1L, 50.0, 100.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 25.0))
                .isEqualTo(IndicatorStatus.GREEN);
    }

    @Test
    void lowerIsBetter_valueEqualToYellow_returnsGreen() {
        stubThresholds(1L, 50.0, 100.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 50.0))
                .isEqualTo(IndicatorStatus.GREEN);
    }

    @Test
    void lowerIsBetter_valueBetweenYellowAndRed_returnsYellow() {
        stubThresholds(1L, 50.0, 100.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 75.0))
                .isEqualTo(IndicatorStatus.YELLOW);
    }

    @Test
    void lowerIsBetter_valueEqualToRed_returnsYellow() {
        stubThresholds(1L, 50.0, 100.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 100.0))
                .isEqualTo(IndicatorStatus.YELLOW);
    }

    @Test
    void lowerIsBetter_valueAboveRed_returnsRed() {
        stubThresholds(1L, 50.0, 100.0);

        assertThat(indicatorStatusService.compute(indicator(1L), 150.0))
                .isEqualTo(IndicatorStatus.RED);
    }

    @Test
    void thresholdsNotFound_throwsEntityNotFoundException() {
        when(thresholdsRepository.findTopByIndicatorIdOrderByRecordedAtDesc(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> indicatorStatusService.compute(indicator(99L), 10.0))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Indicator indicator(Long id) {
        Indicator indicator = new Indicator();
        indicator.setId(id);
        return indicator;
    }

    private Thresholds thresholds(double yellow, double red) {
        Thresholds t = new Thresholds();
        t.setYellowThreshold(yellow);
        t.setRedThreshold(red);
        return t;
    }

    private void stubThresholds(Long indicatorId, double yellow, double red) {
        when(thresholdsRepository.findTopByIndicatorIdOrderByRecordedAtDesc(indicatorId))
                .thenReturn(Optional.of(thresholds(yellow, red)));
    }
}
