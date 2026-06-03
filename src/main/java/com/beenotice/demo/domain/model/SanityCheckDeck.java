package com.beenotice.demo.domain.model;

import java.util.List;

public final class SanityCheckDeck {

    private final List<SanityCheck> checks;

    public SanityCheckDeck(List<SanityCheck> checks) {
        if (checks.isEmpty()) {
            throw new IllegalArgumentException("Deck must not be empty");
        }
        this.checks = List.copyOf(checks);
    }

    public SanityCheck pickAt(int position) {
        return checks.get(Math.floorMod(position, checks.size()));
    }
}
