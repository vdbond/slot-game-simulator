package com.vdbond.stats;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StatisticsAccumulatorTest {

    private static final BigDecimal BET = BigDecimal.TEN;
    private static final double DELTA = 1e-9;

    private static StatisticsAccumulator accumulatorOf(BigDecimal... payouts) {
        StatisticsAccumulator accumulator = new StatisticsAccumulator();
        for (BigDecimal payout : payouts) {
            accumulator.record(payout);
        }
        return accumulator;
    }

    @Test
    void derivesMeanVarianceAndStandardDeviation() {
        // payouts 0, 0, 0, 40 -> mean 10, sample variance (1600 - 40^2/4) / 3 = 400, sd 20
        SimulationStatistics stats = accumulatorOf(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(40)
        ).summarize(BET);

        assertEquals(0, stats.meanPayout().compareTo(BigDecimal.valueOf(10)));
        assertEquals(400.0, stats.variance(), DELTA);
        assertEquals(20.0, stats.standardDeviation(), DELTA);
        assertEquals(2.0, stats.standardDeviationInBets(), DELTA);
    }

    @Test
    void derivesRtpFromWageredAndReturned() {
        SimulationStatistics stats = accumulatorOf(
                BigDecimal.ZERO, BigDecimal.valueOf(15), BigDecimal.valueOf(25), BigDecimal.ZERO, BigDecimal.ZERO
        ).summarize(BET);

        assertEquals(0, stats.totalWagered().compareTo(BigDecimal.valueOf(50)));
        assertEquals(0, stats.totalReturned().compareTo(BigDecimal.valueOf(40)));
        assertEquals(0.8, stats.rtp(), DELTA);
        assertEquals(80.0, stats.rtpPercent(), DELTA);
    }

    @Test
    void emptyAccumulatorSummarizesToZerosWithoutNaN() {
        SimulationStatistics stats = new StatisticsAccumulator().summarize(BET);

        assertEquals(0, stats.rounds());
        assertEquals(0, stats.totalWagered().compareTo(BigDecimal.ZERO));
        assertEquals(0, stats.meanPayout().compareTo(BigDecimal.ZERO));
        assertEquals(0.0, stats.rtp(), DELTA);
        assertEquals(0.0, stats.variance(), DELTA);
        assertEquals(0.0, stats.standardDeviation(), DELTA);
    }

    @Test
    void singleRoundHasZeroVariance() {
        SimulationStatistics stats = accumulatorOf(BigDecimal.valueOf(40)).summarize(BET);

        assertEquals(0, stats.meanPayout().compareTo(BigDecimal.valueOf(40)));
        assertEquals(0.0, stats.variance(), DELTA);
        assertEquals(0.0, stats.standardDeviation(), DELTA);
    }

    @Test
    void countsEveryRoundIncludingLosingOnes() {
        BigDecimal totalPayout = BigDecimal.valueOf(40);
        BigDecimal squaredPayouts = totalPayout.multiply(totalPayout);

        StatisticsAccumulator accumulator = accumulatorOf(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalPayout
        );

        assertEquals(4, accumulator.getRounds());
        assertEquals(0, accumulator.getTotalPayout().compareTo(totalPayout));
        assertEquals(0, accumulator.getSumOfSquaredPayouts().compareTo(squaredPayouts));
    }

}
