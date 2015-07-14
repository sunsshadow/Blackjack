package com.blackjackquiz.app.deck;

import java.util.Random;

class Randomizer {
    static <T> T next(T[] values) {
        return values[random.nextInt(values.length)];
    }

    private Randomizer() {
    }

    private static final Random random = new Random(System.currentTimeMillis());
}
