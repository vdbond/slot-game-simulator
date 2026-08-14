package com.vdbond.domain;

import java.math.BigDecimal;
import java.util.Map;

public record Paytable(Map<Symbol, BigDecimal> payouts) {

    public Paytable {
        if (payouts == null || payouts.isEmpty()) {
            throw new IllegalArgumentException("Paytable must define at least one payout");
        }
    }

    public BigDecimal payoutFor(Symbol symbol) {
        return payouts.getOrDefault(symbol, BigDecimal.ZERO);
    }

}
