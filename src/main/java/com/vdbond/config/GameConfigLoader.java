package com.vdbond.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vdbond.domain.GameConfig;
import com.vdbond.domain.Paytable;
import com.vdbond.domain.ReelStrip;
import com.vdbond.domain.Symbol;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import tools.jackson.databind.json.JsonMapper;

@UtilityClass
public class GameConfigLoader {

    private static final JsonMapper MAPPER = new JsonMapper();

    public static GameConfig loadFromClasspath(String resourcePath) {
        try (InputStream in = GameConfigLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Config resource not found on classpath: " + resourcePath);
            }
            return load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load config from classpath: " + resourcePath, e);
        }
    }

    private static GameConfig load(InputStream in) throws IOException {
        ConfigDto dto = MAPPER.readValue(in, ConfigDto.class);
        return toGameConfig(dto);
    }

    private static GameConfig toGameConfig(ConfigDto dto) {
        Paytable paytable = toPaytable(dto.paytable());
        List<ReelStrip> reels = dto.reels().stream()
                .map(strip -> toReelStrip(strip, paytable))
                .toList();
        return new GameConfig(reels, paytable, dto.bet());
    }

    private static ReelStrip toReelStrip(List<String> symbolCodes, Paytable paytable) {
        List<Symbol> symbols = symbolCodes.stream().map(GameConfigLoader::parseSymbol).toList();
        symbols.forEach(symbol -> requireKnownToPaytable(symbol, paytable));
        return new ReelStrip(symbols);
    }

    private static Paytable toPaytable(Map<String, BigDecimal> rawPayouts) {
        Map<Symbol, BigDecimal> payouts = new EnumMap<>(Symbol.class);
        rawPayouts.forEach((code, payout) -> payouts.put(parseSymbol(code), payout));
        return new Paytable(payouts);
    }

    private static Symbol parseSymbol(String code) {
        try {
            return Symbol.valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown symbol in config: " + code, e);
        }
    }

    private static void requireKnownToPaytable(Symbol symbol, Paytable paytable) {
        if (!paytable.payouts().containsKey(symbol)) {
            throw new IllegalArgumentException("Reel symbol has no paytable entry: " + symbol);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ConfigDto(BigDecimal bet, List<List<String>> reels, Map<String, BigDecimal> paytable) {
    }

}
