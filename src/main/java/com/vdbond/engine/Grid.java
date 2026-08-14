package com.vdbond.engine;

import com.vdbond.domain.GameConfig;
import com.vdbond.domain.ReelStrip;
import com.vdbond.domain.Symbol;

public class Grid {

    public static final int ROWS = 3;

    private final Symbol[][] cells = new Symbol[GameConfig.REEL_COUNT][ROWS];

    public void resolve(GameConfig config, int[] stops) {
        for (int reel = 0; reel < GameConfig.REEL_COUNT; reel++) {
            ReelStrip strip = config.reel(reel);
            for (int row = 0; row < ROWS; row++) {
                cells[reel][row] = strip.symbolAt(stops[reel], row);
            }
        }
    }

    public Symbol symbolAt(int reel, int row) {
        return cells[reel][row];
    }

}
