package com.vdbond.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Symbol {
    W1(true, false),
    H1(false, false),
    H2(false, false),
    H3(false, false),
    L1(false, false),
    L2(false, false),
    L3(false, false),
    L4(false, false),
    SCA(false, true);

    private final boolean wild;
    private final boolean scatter;

    public boolean isReplaceable() {
        return !scatter;
    }

}
