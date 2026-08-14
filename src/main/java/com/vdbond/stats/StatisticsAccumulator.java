package com.vdbond.stats;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class StatisticsAccumulator {

    private long rounds;
    private BigDecimal totalPayout = BigDecimal.ZERO;
    private BigDecimal sumOfSquaredPayouts = BigDecimal.ZERO;

    public void record(BigDecimal payout) {
        rounds++;
        if (payout.signum() != 0) {
            totalPayout = totalPayout.add(payout);
            sumOfSquaredPayouts = sumOfSquaredPayouts.add(payout.multiply(payout));
        }
    }

    public SimulationStatistics summarize(BigDecimal bet) {
        return SimulationStatistics.of(rounds, bet, totalPayout, sumOfSquaredPayouts);
    }

}
