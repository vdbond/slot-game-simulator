package com.vdbond.domain;

public record Payline(int reel0Row, int reel1Row, int reel2Row) {

    public int rowAt(ReelIndex reelIndex) {
        return switch (reelIndex) {
            case ONE -> reel0Row;
            case TWO -> reel1Row;
            case THREE -> reel2Row;
        };
    }

}