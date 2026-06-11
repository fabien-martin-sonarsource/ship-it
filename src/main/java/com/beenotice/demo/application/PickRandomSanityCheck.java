package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.RandomSanityCheckPick;
import com.beenotice.demo.domain.model.SanityCheckDeck;
import com.beenotice.demo.domain.spi.RandomGenerator;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Service;

@Service
public final class PickRandomSanityCheck {

    private final SanityCheckInventory inventory;
    private final RandomGenerator randomGenerator;

    public PickRandomSanityCheck(SanityCheckInventory inventory, RandomGenerator randomGenerator) {
        this.inventory = inventory;
        this.randomGenerator = randomGenerator;
    }

    public RandomSanityCheckPick pick() {
        return new SanityCheckDeck(inventory.findAll()).pickRandom(randomGenerator);
    }
}
