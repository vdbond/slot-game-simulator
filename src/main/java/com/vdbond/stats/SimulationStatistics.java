package com.vdbond.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public record SimulationStatistics(
        long rounds,
        BigDecimal bet,
        BigDecimal totalReturned,
        BigDecimal meanPayout,
        double variance,
        double standardDeviation
) {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final int MEAN_SCALE = 2;

    public static SimulationStatistics of(
            long rounds,
            BigDecimal bet,
            BigDecimal totalPayout,
            BigDecimal sumOfSquaredPayouts
    ) {
        if (rounds <= 0) {
            return new SimulationStatistics(0, bet, BigDecimal.ZERO, BigDecimal.ZERO, 0.0, 0.0);
        }

        BigDecimal divisor = BigDecimal.valueOf(rounds);
        BigDecimal meanPayout = totalPayout.divide(divisor, MEAN_SCALE, RoundingMode.HALF_EVEN);

        double variance = 0.0;
        if (rounds > 1) {
            BigDecimal sumOfSquaredDeviations =
                    sumOfSquaredPayouts.subtract(totalPayout.multiply(totalPayout).divide(divisor, MATH_CONTEXT));
            variance = Math.max(
                    sumOfSquaredDeviations.divide(BigDecimal.valueOf(rounds - 1), MATH_CONTEXT).doubleValue(), 0.0);
        }

        return new SimulationStatistics(rounds, bet, totalPayout, meanPayout, variance, Math.sqrt(variance));
    }

    public BigDecimal totalWagered() {
        return bet.multiply(BigDecimal.valueOf(rounds));
    }

    public double rtp() {
        BigDecimal wagered = totalWagered();
        return wagered.signum() == 0 ? 0.0 : totalReturned.divide(wagered, MATH_CONTEXT).doubleValue();
    }

    public double rtpPercent() {
        return rtp() * 100.0;
    }

    public double standardDeviationInBets() {
        return bet.signum() == 0 ? 0.0 : standardDeviation / bet.doubleValue();
    }

}
