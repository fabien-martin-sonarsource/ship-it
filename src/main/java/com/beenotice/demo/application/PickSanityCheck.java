package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.model.SanityCheckDeck;
import com.beenotice.demo.domain.model.SanityCheckPick;
import com.beenotice.demo.domain.spi.RandomGenerator;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Service;

@Service
public final class PickSanityCheck {

    private final SanityCheckInventory inventory;
    private final RandomGenerator random;

    public PickSanityCheck(SanityCheckInventory inventory, RandomGenerator random) {
        this.inventory = inventory;
        this.random = random;
    }

    public SanityCheck atPosition(int position) {
        return new SanityCheckDeck(inventory.findAll()).pickAt(position);
    }

    public SanityCheckPick random() {
        return new SanityCheckDeck(inventory.findAll()).pickRandom(random);
    }
}
