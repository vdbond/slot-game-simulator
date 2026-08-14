package com.vdbond.report;

import com.vdbond.stats.SimulationStatistics;
import com.vdbond.stats.StatisticsAccumulator;
import java.math.BigDecimal;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ReportFormatterTest {

    private static final BigDecimal BET = BigDecimal.TEN;

    private static StatisticsAccumulator accumulatorOf(BigDecimal... payouts) {
        StatisticsAccumulator accumulator = new StatisticsAccumulator();
        for (BigDecimal payout : payouts) {
            accumulator.record(payout);
        }
        return accumulator;
    }

    @Test
    void includesAllRequiredFields() {
        // payouts 0, 0, 0, 40 on a bet of 10 -> 4 rounds, wagered 40, returned 40, rtp 100%, sd 20
        SimulationStatistics stats = accumulatorOf(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(40)
        ).summarize(BET);

        String report = ReportFormatter.format(stats, Duration.ofMillis(1234));

        assertTrue(report.contains("Rounds:              4"));
        assertTrue(report.contains("Total Wagered:       40.00 credits"));
        assertTrue(report.contains("Total Returned:      40.00 credits"));
        assertTrue(report.contains("RTP:                 100.0000%"));
        assertTrue(report.contains("Standard Deviation:  20.0000 credits (2.0000x bet)"));
        assertTrue(report.contains("Execution Time:      1.23s"));
    }

    @Test
    void formatsExecutionTimeWithMinutes() {
        SimulationStatistics stats = accumulatorOf(BigDecimal.ZERO).summarize(BET);

        String report = ReportFormatter.format(stats, Duration.ofSeconds(90));

        assertTrue(report.contains("Execution Time:      1m 30.00s"));
    }

}