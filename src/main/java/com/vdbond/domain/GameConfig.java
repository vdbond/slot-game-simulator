package com.vdbond.domain;

import java.math.BigDecimal;
import java.util.List;

public record GameConfig(List<ReelStrip> reels, Paytable paytable, BigDecimal bet) {

    public static final int REEL_COUNT = 3;

    public GameConfig {
        if (reels == null || reels.size() != REEL_COUNT) {
            throw new IllegalArgumentException("GameConfig requires exactly " + REEL_COUNT + " reels");
        }
        if (paytable == null) {
            throw new IllegalArgumentException("GameConfig requires a paytable");
        }
        if (bet == null || bet.signum() <= 0) {
            throw new IllegalArgumentException("GameConfig requires a positive bet");
        }
    }

    public ReelStrip reel(int reelIndex) {
        return reels.get(reelIndex);
    }

}
