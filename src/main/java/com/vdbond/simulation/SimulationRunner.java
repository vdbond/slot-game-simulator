package com.vdbond.simulation;

import com.vdbond.domain.GameConfig;
import com.vdbond.engine.Grid;
import com.vdbond.engine.SpinGenerator;
import com.vdbond.engine.WinEvaluator;
import com.vdbond.stats.StatisticsAccumulator;

import java.math.BigDecimal;
import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SimulationRunner {

    private final GameConfig config;
    private final RandomGenerator random;

    public StatisticsAccumulator run(long rounds) {
        SpinGenerator spinGenerator = new SpinGenerator(config, random);
        WinEvaluator evaluator = new WinEvaluator();
        StatisticsAccumulator accumulator = new StatisticsAccumulator();
        Grid grid = new Grid();
        int[] stops = new int[GameConfig.REEL_COUNT];

        for (long round = 0; round < rounds; round++) {
            spinGenerator.spin(stops);
            grid.resolve(config, stops);
            BigDecimal payout = evaluator.evaluate(grid, config.paytable());
            accumulator.record(payout);
        }

        return accumulator;
    }

}