package com.vdbond.engine;

import com.vdbond.domain.GameConfig;
import com.vdbond.domain.Paytable;
import com.vdbond.domain.ReelStrip;
import com.vdbond.domain.Symbol;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class WinEvaluatorTest {

    private static final Paytable PAYTABLE = new Paytable(Map.of(
            Symbol.W1, new BigDecimal("2000"),
            Symbol.H1, new BigDecimal("800"),
            Symbol.H2, new BigDecimal("500"),
            Symbol.H3, new BigDecimal("80"),
            Symbol.L1, new BigDecimal("50"),
            Symbol.L2, new BigDecimal("20"),
            Symbol.L3, new BigDecimal("15"),
            Symbol.L4, new BigDecimal("10"),
            Symbol.SCA, new BigDecimal("200")
    ));

    private final WinEvaluator evaluator = new WinEvaluator();

    private static Grid gridOf(Symbol[] reel0, Symbol[] reel1, Symbol[] reel2) {
        GameConfig config = new GameConfig(
                List.of(new ReelStrip(List.of(reel0)), new ReelStrip(List.of(reel1)), new ReelStrip(List.of(reel2))),
                PAYTABLE,
                BigDecimal.TEN
        );
        Grid grid = new Grid();
        grid.resolve(config, new int[]{0, 0, 0});
        return grid;
    }

    @Test
    void nonWinningGridPaysNothing() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.H3,
                        Symbol.H1,
                        Symbol.H2
                },
                new Symbol[]{
                        Symbol.L2,
                        Symbol.W1,
                        Symbol.L3
                },
                new Symbol[]{
                        Symbol.L4,
                        Symbol.L4,
                        Symbol.L4
                }
        );

        assertEquals(BigDecimal.ZERO, evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void oneLineWinPaysBottomRowThreeOfAKind() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.L3,
                        Symbol.L2,
                        Symbol.L3
                },
                new Symbol[]{
                        Symbol.L4,
                        Symbol.L4,
                        Symbol.L3
                },
                new Symbol[]{
                        Symbol.L1,
                        Symbol.L4,
                        Symbol.L3
                }
        );

        assertEquals(PAYTABLE.payoutFor(Symbol.L3), evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void threeLineWinSumsEveryWinningLine() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.H3,
                        Symbol.H3,
                        Symbol.H3
                },
                new Symbol[]{
                        Symbol.L4,
                        Symbol.H3,
                        Symbol.L1
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.H3,
                        Symbol.H3
                }
        );

        assertEquals(
                PAYTABLE.payoutFor(Symbol.H3).multiply(BigDecimal.valueOf(3)),
                evaluator.evaluate(grid, PAYTABLE)
        );
    }

    @Test
    void bonusTriggerAddsLineWinAndScatterWin() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.SCA,
                        Symbol.L2,
                        Symbol.L2
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.SCA,
                        Symbol.L2
                },
                new Symbol[]{
                        Symbol.L1,
                        Symbol.SCA,
                        Symbol.L2
                }
        );

        assertEquals(
                PAYTABLE.payoutFor(Symbol.L2).add(PAYTABLE.payoutFor(Symbol.SCA)),
                evaluator.evaluate(grid, PAYTABLE)
        );
    }

    @Test
    void wildCompletesThreeOfAKind() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.H3,
                        Symbol.L1,
                        Symbol.L4
                },
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L2,
                        Symbol.L1
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.L3,
                        Symbol.L2
                }
        );

        assertEquals(PAYTABLE.payoutFor(Symbol.H3), evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void allWildLinePaysWildsOwnTopTier() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L1,
                        Symbol.L4
                },
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L2,
                        Symbol.L1
                },
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L3,
                        Symbol.L4
                }
        );

        assertEquals(PAYTABLE.payoutFor(Symbol.W1), evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void wildDoesNotBridgeTwoDifferentSymbols() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L1,
                        Symbol.L4
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.L2,
                        Symbol.L1
                },
                new Symbol[]{
                        Symbol.H1,
                        Symbol.L3,
                        Symbol.L4
                }
        );

        assertEquals(BigDecimal.ZERO, evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void wildDoesNotCountAsScatterAndScatterBreaksALine() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.W1,
                        Symbol.L1,
                        Symbol.L4
                },
                new Symbol[]{
                        Symbol.SCA,
                        Symbol.L2,
                        Symbol.L1
                },
                new Symbol[]{
                        Symbol.SCA,
                        Symbol.L3,
                        Symbol.L4
                }
        );

        assertEquals(BigDecimal.ZERO, evaluator.evaluate(grid, PAYTABLE));
    }

    @Test
    void multipleWinningLinesAndScatterAllSumTogether() {
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.H3,
                        Symbol.SCA,
                        Symbol.L2
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.SCA,
                        Symbol.L2
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.SCA,
                        Symbol.L2
                }
        );

        assertEquals(
                PAYTABLE.payoutFor(Symbol.H3)
                        .add(PAYTABLE.payoutFor(Symbol.L2))
                        .add(PAYTABLE.payoutFor(Symbol.SCA)),
                evaluator.evaluate(grid, PAYTABLE)
        );
    }

    @Test
    void winningsNotOnOneRowIsProperlyEvaluated() {
        // H3  H1 H3
        // SCA H3 H1
        // H3  L2 H3
        // 0, 1, 2 and 2, 1, 0 win and are supposed to be summed up
        Grid grid = gridOf(
                new Symbol[]{
                        Symbol.H3,
                        Symbol.SCA,
                        Symbol.H3
                },
                new Symbol[]{
                        Symbol.H1,
                        Symbol.H3,
                        Symbol.L2
                },
                new Symbol[]{
                        Symbol.H3,
                        Symbol.H1,
                        Symbol.H3
                }
        );

        assertEquals(
                PAYTABLE.payoutFor(Symbol.H3).add(PAYTABLE.payoutFor(Symbol.H3)),
                evaluator.evaluate(grid, PAYTABLE)
        );
    }

}
