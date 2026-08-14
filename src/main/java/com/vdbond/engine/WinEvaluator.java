package com.vdbond.engine;

import com.vdbond.domain.GameConfig;
import com.vdbond.domain.Payline;
import com.vdbond.domain.Paylines;
import com.vdbond.domain.Paytable;
import com.vdbond.domain.ReelIndex;
import com.vdbond.domain.Symbol;

import java.math.BigDecimal;

public class WinEvaluator {

    private static final int SCATTER_MIN_COUNT = 3;

    public BigDecimal evaluate(Grid grid, Paytable paytable) {
        BigDecimal total = BigDecimal.ZERO;
        for (Payline payline : Paylines.STANDARD) {
            total = total.add(evaluateLine(grid, payline, paytable));
        }
        return total.add(evaluateScatter(grid, paytable));
    }

    private BigDecimal evaluateLine(Grid grid, Payline payline, Paytable paytable) {
        Symbol reelOneSymbol = grid.symbolAt(0, payline.rowAt(ReelIndex.ONE));
        Symbol reelTwoSymbol = grid.symbolAt(1, payline.rowAt(ReelIndex.TWO));
        Symbol reelThreeSymbol = grid.symbolAt(2, payline.rowAt(ReelIndex.THREE));

        if (!reelOneSymbol.isReplaceable() || !reelTwoSymbol.isReplaceable() || !reelThreeSymbol.isReplaceable()) {
            // scatter on the line breaks it - scatter only pays grid-wide, never on a line
            return BigDecimal.ZERO;
        }

        Symbol matched = matchedSymbol(reelOneSymbol, reelTwoSymbol, reelThreeSymbol);
        return matched == null ? BigDecimal.ZERO : paytable.payoutFor(matched);
    }

    private Symbol matchedSymbol(Symbol reelOneSymbo, Symbol reelTwoSymbol, Symbol reelThreeSymbol) {
        Symbol matched = null;

        if (!reelOneSymbo.isWild()) {
            matched = reelOneSymbo;
        }
        if (!reelTwoSymbol.isWild()) {
            if (matched == null) {
                matched = reelTwoSymbol;
            } else if (matched != reelTwoSymbol) {
                return null;
            }
        }
        if (!reelThreeSymbol.isWild()) {
            if (matched == null) {
                matched = reelThreeSymbol;
            } else if (matched != reelThreeSymbol) {
                return null;
            }
        }

        return matched != null ? matched : Symbol.W1;
    }

    private BigDecimal evaluateScatter(Grid grid, Paytable paytable) {
        int count = 0;
        for (int reel = 0; reel < GameConfig.REEL_COUNT; reel++) {
            for (int row = 0; row < Grid.ROWS; row++) {
                if (grid.symbolAt(reel, row).isScatter()) {
                    count++;
                }
            }
        }
        return count >= SCATTER_MIN_COUNT ? paytable.payoutFor(Symbol.SCA) : BigDecimal.ZERO;
    }

}
