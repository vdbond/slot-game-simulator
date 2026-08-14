package com.vdbond;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CliOptionsTest {

    private static final long DEFAULT_ROUNDS_COUNT = 10_000_000L;
    private static final String DEFAULT_CONFIG_FILE_PATH = "game-config.json";

    @Test
    void defaultsWhenNoArgsGiven() {
        CliOptions options = CliOptions.parse(new String[]{});

        assertEquals(DEFAULT_ROUNDS_COUNT, options.rounds());
        assertEquals(DEFAULT_CONFIG_FILE_PATH, options.configResource());
    }

    @Test
    void parsesRoundsFlag() {
        CliOptions options = CliOptions.parse(new String[]{"--rounds", "42"});

        assertEquals(42L, options.rounds());
        assertEquals(DEFAULT_CONFIG_FILE_PATH, options.configResource());
    }

    @Test
    void parsesConfigFlag() {
        CliOptions options = CliOptions.parse(new String[]{"--config", "custom-config.json"});

        assertEquals(DEFAULT_ROUNDS_COUNT, options.rounds());
        assertEquals("custom-config.json", options.configResource());
    }

    @Test
    void parsesBothFlagsInEitherOrder() {
        CliOptions options = CliOptions.parse(new String[]{"--config", "custom-config.json", "--rounds", "7"});

        assertEquals(7L, options.rounds());
        assertEquals("custom-config.json", options.configResource());
    }

    @Test
    void rejectsNonNumericRounds() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--rounds", "abc"}));
    }

    @Test
    void rejectsZeroOrNegativeRounds() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--rounds", "0"}));
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--rounds", "-5"}));
    }

    @Test
    void rejectsFlagMissingValue() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--rounds"}));
    }

    @Test
    void rejectsUnknownFlag() {
        assertThrows(IllegalArgumentException.class, () -> CliOptions.parse(new String[]{"--bet", "10"}));
    }

}