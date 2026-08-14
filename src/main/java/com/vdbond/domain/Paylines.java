package com.vdbond.domain;

import java.util.List;

public class Paylines {

    public static final List<Payline> STANDARD = List.of(
            new Payline(0, 0, 0), // 1: top row straight
            new Payline(1, 1, 1), // 2: middle row straight
            new Payline(2, 2, 2), // 3: bottom row straight
            new Payline(0, 1, 2), // 4: top, middle, bottom
            new Payline(2, 1, 0)  // 5: bottom, middle, top
    );

    private Paylines() {
    }

}
