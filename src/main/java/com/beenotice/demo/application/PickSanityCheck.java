package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.model.SanityCheckDeck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Service;

@Service
public final class PickSanityCheck {

    private final SanityCheckInventory inventory;

    public PickSanityCheck(SanityCheckInventory inventory) {
        this.inventory = inventory;
    }

    public SanityCheck atPosition(int position) {
        return new SanityCheckDeck(inventory.findAll()).pickAt(position);
    }
}
