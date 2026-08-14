package com.vdbond.domain;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ReelStripTest {

    @Test
    void readsSequentialSymbolsFromStop() {
        ReelStrip strip = new ReelStrip(List.of(Symbol.L1, Symbol.L2, Symbol.L3, Symbol.L4, Symbol.H1));

        assertEquals(Symbol.L2, strip.symbolAt(1, 0));
        assertEquals(Symbol.L3, strip.symbolAt(1, 1));
        assertEquals(Symbol.L4, strip.symbolAt(1, 2));
    }

    @Test
    void wrapsAroundPastTheEndOfTheStrip() {
        ReelStrip strip = new ReelStrip(List.of(Symbol.L1, Symbol.L2, Symbol.L3, Symbol.L4, Symbol.H1));

        // stop at the last index (4) reads indices 4, 0, 1
        assertEquals(Symbol.H1, strip.symbolAt(4, 0));
        assertEquals(Symbol.L1, strip.symbolAt(4, 1));
        assertEquals(Symbol.L2, strip.symbolAt(4, 2));
    }

}
