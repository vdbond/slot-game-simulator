package com.vdbond.domain;

import java.util.List;

public record ReelStrip(List<Symbol> symbols) {

    public ReelStrip {
        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("Reel strip must contain at least one symbol");
        }
    }

    public int length() {
        return symbols.size();
    }

    public Symbol symbolAt(int stop, int row) {
        return symbols.get((stop + row) % symbols.size());
    }

}