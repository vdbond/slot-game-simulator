package com.vdbond.simulation;

import com.vdbond.config.GameConfigLoader;
import com.vdbond.domain.GameConfig;
import com.vdbond.stats.SimulationStatistics;
import com.vdbond.stats.StatisticsAccumulator;
import java.math.BigDecimal;
import java.security.SecureRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.RepeatedTest;

class SimulationRunnerTest {

    private static final long ROUNDS = 2_000_000L;
    private static final double EXPECTED_RTP = 93.36d;
    private static final double DELTA = 2.0d;

    private static final GameConfig CONFIG = GameConfigLoader.loadFromClasspath("game-config.json");
    private static final SimulationRunner RUNNER = new SimulationRunner(CONFIG, new SecureRandom());

    @RepeatedTest(50)
    void runsRequestedRoundsAndConvergesNearTheoreticalRtp() {
        StatisticsAccumulator accumulator = RUNNER.run(ROUNDS);

        assertEquals(ROUNDS, accumulator.getRounds());

        SimulationStatistics stats = accumulator.summarize(CONFIG.bet());
        assertEquals(CONFIG.bet().multiply(BigDecimal.valueOf(ROUNDS)), stats.totalWagered());

        assertEquals(EXPECTED_RTP, stats.rtpPercent(), DELTA);
    }

}
