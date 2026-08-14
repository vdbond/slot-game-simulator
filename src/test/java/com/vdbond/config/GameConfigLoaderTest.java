package com.vdbond.config;

import com.vdbond.domain.GameConfig;
import com.vdbond.domain.Symbol;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class GameConfigLoaderTest {

    @Test
    void loadsReelsPaytableAndBetFromClasspathConfig() {
        GameConfig config = GameConfigLoader.loadFromClasspath("test-game-config.json");

        assertEquals(new BigDecimal("10"), config.bet());
        assertEquals(3, config.reels().size());
        assertEquals(61, config.reel(0).length());
        assertEquals(58, config.reel(1).length());
        assertEquals(54, config.reel(2).length());

        assertEquals(new BigDecimal("2000"), config.paytable().payoutFor(Symbol.W1));
        assertEquals(new BigDecimal("200"), config.paytable().payoutFor(Symbol.SCA));

        assertEquals(Symbol.L4, config.reel(0).symbolAt(0, 0));
        assertEquals(Symbol.L3, config.reel(0).symbolAt(0, 1));
        assertEquals(Symbol.L2, config.reel(0).symbolAt(0, 2));
    }

}
