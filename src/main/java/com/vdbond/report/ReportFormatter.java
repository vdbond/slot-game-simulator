package com.vdbond.report;

import com.vdbond.stats.SimulationStatistics;

import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportFormatter {

    public static String format(SimulationStatistics stats, Duration elapsed) {
        return """
                === Slot Machine Simulation Report ===
                Rounds:              %,d
                Total Wagered:       %,.2f credits
                Total Returned:      %,.2f credits
                RTP:                 %.4f%%
                Standard Deviation:  %.4f credits (%.4fx bet)
                Execution Time:      %s
                """.formatted(
                stats.rounds(),
                stats.totalWagered(),
                stats.totalReturned(),
                stats.rtpPercent(),
                stats.standardDeviation(),
                stats.standardDeviationInBets(),
                formatDuration(elapsed));
    }

    private static String formatDuration(Duration elapsed) {
        long totalMillis = elapsed.toMillis();
        long minutes = totalMillis / 60_000;
        double seconds = (totalMillis % 60_000) / 1000.0;
        return minutes > 0 ? "%dm %.2fs".formatted(minutes, seconds) : "%.2fs".formatted(seconds);
    }

}
