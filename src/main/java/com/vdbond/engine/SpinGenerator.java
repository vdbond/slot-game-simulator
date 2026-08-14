package com.vdbond.engine;

import com.vdbond.domain.GameConfig;

import java.util.random.RandomGenerator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpinGenerator {

    private final GameConfig config;
    private final RandomGenerator random;

    public void spin(int[] stopsOut) {
        for (int reel = 0; reel < GameConfig.REEL_COUNT; reel++) {
            stopsOut[reel] = random.nextInt(config.reel(reel).length());
        }
    }

}
